/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.cookies;

import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsWithHeader;
import org.takes.rs.RsWrap;

/**
 * Response decorator, with an additional cookie.
 *
 * The decorator validates cookie name according
 * to <a href="http://tools.ietf.org/html/rfc2616#section-2.2">RFC 2616</a>
 * and cookie value according
 * to <a href="http://tools.ietf.org/html/rfc6265#section-4.1.1">RFC 6265</a>
 *
 * <p>Use this decorator in order to return a response with a "Set-Cookie"
 * header inside, for example:
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
     * Cookie header name.
     */
    private static final CharSequence SET_COOKIE = "Set-Cookie";

    /**
     * Ctor.
     * @param name Cookie name
     * @param value Value of it
     * @param attrs Optional attributes, for example "Path=/"
     */
    public RsWithCookie(final CharSequence name, final CharSequence value,
        final CharSequence... attrs) {
        this(new RsEmpty(), name, value, attrs);
    }

    /**
     * Ctor.
     * @param res Original response
     * @param name Cookie name
     * @param value Value of it
     * @param attrs Optional attributes, for example "Path=/"
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public RsWithCookie(final Response res, final CharSequence name,
        final CharSequence value, final CharSequence... attrs) {
        super(
            new RsWithHeader(
                res,
                RsWithCookie.SET_COOKIE,
                RsWithCookie.make(
                    RsWithCookie.validName(name),
                    RsWithCookie.validValue(value),
                    attrs
                )
            )
        );
    }

    /**
     * Build cookie string.
     * @param name Cookie name
     * @param value Value of it
     * @param attrs Optional attributes, for example "Path=/"
     * @return Text
     */
    private static String make(final CharSequence name,
        final CharSequence value, final CharSequence... attrs) {
        final StringBuilder text = new StringBuilder(
            String.format("%s=%s;", name, value)
        );
        for (final CharSequence attr : attrs) {
            text.append(attr).append(';');
        }
        return text.toString();
    }

    /**
     * Checks value according RFC 6265 section 4.1.1.
     * @param value Cookie value
     * @return Cookie value
     */
    private static CharSequence validValue(final CharSequence value) {
        if (!RsWithCookie.CVALUE_PTRN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                String.format(
                    "Cookie value \"%s\" contains invalid characters",
                    value
                )
            );
        }
        return value;
    }

    /**
     * Checks name according RFC 2616, section 2.2.
     * @param name Cookie name;
     * @return Cookie name
     */
    private static CharSequence validName(final CharSequence name) {
        if (!RsWithCookie.CNAME_PTRN.matcher(name).matches()) {
            throw new IllegalArgumentException(
                String.format(
                    "Cookie name \"%s\" contains invalid characters",
                    name
                )
            );
        }
        return name;
    }
}
