/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.hamcrest;

import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsEqual;
import org.takes.Request;

/**
 * Matcher for a single cookie in a request's {@code Cookie} header.
 *
 * <p>Verifies only the cookie's name and value. Multiple cookies packed
 * into one {@code Cookie} header (separated by {@code ;}) or spread across
 * several {@code Cookie} headers are all searched. Use it instead of a
 * hand-rolled {@link HmHeader} assertion against {@code Cookie}:
 *
 * <pre> MatcherAssert.assertThat(
 *     request,
 *     new HmRqCookie("session", "abc")
 * );</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 2.0
 */
public final class HmRqCookie extends TypeSafeMatcher<Request> {

    /**
     * Cookie header name.
     */
    private static final String COOKIE = "cookie";

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
    public HmRqCookie(final String cookie, final String val) {
        this(new IsEqual<>(cookie), new IsEqual<>(val));
    }

    /**
     * Ctor.
     * @param cookie Exact cookie name
     * @param val Cookie value matcher
     */
    public HmRqCookie(final String cookie, final Matcher<String> val) {
        this(new IsEqual<>(cookie), val);
    }

    /**
     * Ctor.
     * @param cookie Cookie name matcher
     * @param val Cookie value matcher
     */
    public HmRqCookie(final Matcher<String> cookie, final Matcher<String> val) {
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
    public boolean matchesSafely(final Request request) {
        try {
            boolean matched = false;
            for (final String header : request.head()) {
                if (!HmRqCookie.isCookie(header) || header.indexOf(':') < 0) {
                    continue;
                }
                if (this.matchAny(HmRqCookie.crumbs(header))) {
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
    public void describeMismatchSafely(final Request request,
        final Description description) {
        if (this.actual == null) {
            description.appendText("no Cookie with name: ")
                .appendDescriptionOf(this.name);
        } else {
            description.appendText("cookie with name: ")
                .appendDescriptionOf(this.name)
                .appendText(" had value: ")
                .appendValue(this.actual);
        }
    }

    private boolean matchAny(final Iterable<String> crumbs) {
        boolean matched = false;
        for (final String crumb : crumbs) {
            if (this.matchCrumb(crumb.trim())) {
                matched = true;
                break;
            }
        }
        return matched;
    }

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

    private static boolean isCookie(final String header) {
        final int colon = header.indexOf(':');
        return colon >= 0
            && HmRqCookie.COOKIE.equalsIgnoreCase(
                header.substring(0, colon).trim()
            );
    }

    private static Iterable<String> crumbs(final String header) {
        return Arrays.asList(
            header.substring(header.indexOf(':') + 1).split(";")
        );
    }
}
