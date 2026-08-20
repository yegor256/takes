/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.cookies;

import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;
import org.takes.Response;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsWrap;

/**
 * A response decorator that adds HTTP cookies to responses.
 *
 * <p>This decorator validates cookie names according to
 * <a href="http://tools.ietf.org/html/rfc2616#section-2.2">RFC 2616</a>
 * and cookie values according to
 * <a href="http://tools.ietf.org/html/rfc6265#section-4.1.1">RFC 6265</a>.
 * It adds a Set-Cookie header to responses with the specified cookie name,
 * value, and optional attributes.
 *
 * <p>Use this decorator to return a response with a "Set-Cookie"
 * header, for example:
 *
 * <pre> return new RsWithCookie(
 *   new RsText("hello, world!"),
 *   "u", "Jeff",
 *   "Path=/", "Expires=Wed, 13 Jan 2021 22:23:01 GMT"
 * );</pre>
 *
 * <p>This response will contain this header:
 *
 * <pre> Set-Cookie: u=Jeff;Path=/;Expires=Wed, 13 Jan 2021 22:23:01 GMT</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsWithCookie extends RsWrap {

    /**
     * Cookie header name.
     */
    static final CharSequence SET_COOKIE = "Set-Cookie";

    /**
     * Cookie value validation regexp.
     */
    private static final Pattern CVALUE_PTRN = Pattern.compile(
        "[\\x21\\x23-\\x2B\\x2D-\\x3A\\x3C-\\x5B\\x5D-\\x7E]*|\"[\\x21\\x23-\\x2B\\x2D-\\x3A\\x3C-\\x5B\\x5D-\\x7E]*\""
    );

    /**
     * Cookie name validation regexp.
     */
    private static final Pattern CNAME_PTRN = Pattern.compile(
        "[\\x20-\\x7E&&[^()<>@,;:\\\"/\\[\\]?={} ]]+"
    );

    /**
     * Constructor with cookie name, value, and optional attributes.
     * @param name The cookie name
     * @param value The cookie value
     * @param attrs Optional cookie attributes such as "Path=/" or "Secure"
     */
    public RsWithCookie(final CharSequence name, final CharSequence value,
        final CharSequence... attrs) {
        this(new RsEmpty(), name, value, attrs);
    }

    /**
     * Constructor that decorates an existing response with a cookie.
     * @param res The original response to decorate
     * @param name The cookie name
     * @param value The cookie value
     * @param attrs Optional cookie attributes such as "Path=/" or "Secure"
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public RsWithCookie(final Response res, final CharSequence name,
        final CharSequence value, final CharSequence... attrs) {
        super(new LazyResponse(res, name, value, attrs));
    }

    static String make(final CharSequence name,
        final CharSequence value, final CharSequence... attrs) {
        final StringBuilder text = new StringBuilder(
            new UncheckedText(new FormattedText("%s=%s;", name, value)).asString()
        );
        for (final CharSequence attr : attrs) {
            text.append(attr).append(';');
        }
        return text.toString();
    }

    static CharSequence validValue(final CharSequence value) {
        if (!RsWithCookie.CVALUE_PTRN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                new UncheckedText(
                    new FormattedText(
                        "Cookie value \"%s\" contains invalid characters",
                        value
                    )
                ).asString()
            );
        }
        return value;
    }

    static CharSequence validName(final CharSequence name) {
        if (!RsWithCookie.CNAME_PTRN.matcher(name).matches()) {
            throw new IllegalArgumentException(
                new UncheckedText(
                    new FormattedText(
                        "Cookie name \"%s\" contains invalid characters",
                        name
                    )
                ).asString()
            );
        }
        return name;
    }
}
