/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.hamcrest;

import java.io.IOException;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.takes.Response;

/**
 * Matcher for a single cookie in a response's {@code Set-Cookie} header.
 *
 * <p>Verifies only the cookie's name and value, ignoring attributes such as
 * {@code Path}, {@code Domain}, {@code HttpOnly}, {@code Secure}, or
 * {@code Expires}. Use it instead of a hand-rolled {@link HmHeader}
 * assertion against {@code Set-Cookie}:
 *
 * <pre> MatcherAssert.assertThat(
 *     response,
 *     new HmRsCookie("session", "abc")
 * );</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 2.0
 */
public final class HmRsCookie extends TypeSafeMatcher<Response> {

    /**
     * Set-Cookie header name.
     */
    private static final String SET_COOKIE = "set-cookie";

    /**
     * Cookie name matcher.
     */
    private final Matcher<String> name;

    /**
     * Cookie value matcher.
     */
    private final Matcher<String> value;

    /**
     * Value observed for the cookie, kept for mismatch description.
     */
    private String actual;

    /**
     * Ctor.
     * @param cookie Exact cookie name
     * @param val Exact cookie value
     */
    public HmRsCookie(final String cookie, final String val) {
        this(Matchers.equalTo(cookie), Matchers.equalTo(val));
    }

    /**
     * Ctor.
     * @param cookie Exact cookie name
     * @param val Cookie value matcher
     */
    public HmRsCookie(final String cookie, final Matcher<String> val) {
        this(Matchers.equalTo(cookie), val);
    }

    /**
     * Ctor.
     * @param cookie Cookie name matcher
     * @param val Cookie value matcher
     */
    public HmRsCookie(final Matcher<String> cookie, final Matcher<String> val) {
        super();
        this.name = cookie;
        this.value = val;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("cookie with name: ")
            .appendDescriptionOf(this.name)
            .appendText(" and value: ")
            .appendDescriptionOf(this.value);
    }

    @Override
    public boolean matchesSafely(final Response response) {
        try {
            boolean matched = false;
            for (final String header : response.head()) {
                if (!HmRsCookie.isSetCookie(header)) {
                    continue;
                }
                if (this.matchCrumb(HmRsCookie.firstCrumb(header))) {
                    matched = true;
                    break;
                }
            }
            return matched;
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void describeMismatchSafely(final Response response,
        final Description description) {
        if (this.actual == null) {
            description.appendText("no Set-Cookie with name: ")
                .appendDescriptionOf(this.name);
        } else {
            description.appendText("cookie with name: ")
                .appendDescriptionOf(this.name)
                .appendText(" had value: ")
                .appendValue(this.actual);
        }
    }

    /**
     * Tries to match a single name=value pair against this matcher, storing
     * the observed value if the name is a match.
     * @param crumb Name/value pair as "name=value"
     * @return True if both name and value match
     */
    private boolean matchCrumb(final String crumb) {
        final int sign = crumb.indexOf('=');
        boolean matched = false;
        if (sign >= 0
            && this.name.matches(crumb.substring(0, sign).trim())) {
            final String observed = crumb.substring(sign + 1).trim();
            this.actual = observed;
            matched = this.value.matches(observed);
        }
        return matched;
    }

    /**
     * Checks whether a header line is a {@code Set-Cookie} header.
     * @param header Raw header line ("Name: value")
     * @return True if the header name equals "Set-Cookie" ignoring case
     */
    private static boolean isSetCookie(final String header) {
        final int colon = header.indexOf(':');
        return colon >= 0
            && HmRsCookie.SET_COOKIE.equalsIgnoreCase(
                header.substring(0, colon).trim()
            );
    }

    /**
     * Extracts the first {@code name=value} crumb from a Set-Cookie header,
     * stripping off any {@code ;}-separated attributes.
     * @param header Raw header line ("Set-Cookie: name=value; Path=/")
     * @return The first crumb ("name=value")
     */
    private static String firstCrumb(final String header) {
        return header.substring(header.indexOf(':') + 1)
            .trim().split(";", 2)[0].trim();
    }
}
