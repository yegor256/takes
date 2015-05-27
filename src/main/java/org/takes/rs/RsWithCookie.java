/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.rs;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.takes.Response;

/**
 * Response decorator, with an additional cookie.
 *
 * The decorator validates cookie name according
 * @see <a href="http://tools.ietf.org/html/rfc2616#section-2.2">RFC 2616</a>
 * and cookie value according
 * @see <a href="http://tools.ietf.org/html/rfc6265#section-4.1.1">RFC 6265</a>
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
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class RsWithCookie extends RsWrap {

    /**
     * Cookie value validation regexp.
     * @checkstyle LineLengthCheck (3 lines)
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
     * Pattern to get the current cookie value from header.
     */
    private static final Pattern COOKIE_PTRN = Pattern.compile(
        String.format("%s: ([^\\s].*)", RsWithCookie.SET_COOKIE)
    );

    /**
     * Ctor.
     * @param name Cookie name
     * @param value Value of it
     * @param attrs Optional attributes, for example "Path=/"
     */
    public RsWithCookie(final CharSequence name, final CharSequence value,
        final CharSequence... attrs) {
        this(
            new RsEmpty(),
            RsWithCookie.checkName(name),
            RsWithCookie.checkValue(value),
            attrs
        );
    }

    /**
     * Ctor.
     * @param res Original response
     * @param name Cookie name
     * @param value Value of it
     * @param attrs Optional attributes, for example "Path=/"
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public RsWithCookie(final Response res, final CharSequence name,
        final CharSequence value, final CharSequence... attrs) {
        super(
            new Response() {
                @Override
                public Iterable<String> head() throws IOException {
                    return RsWithCookie.extend(
                        res,
                        name,
                        value,
                        attrs
                    );
                }
                @Override
                public InputStream body() throws IOException {
                    return res.body();
                }
            }
        );
        RsWithCookie.checkName(name);
        RsWithCookie.checkValue(value);
    }

    /**
     * Add an additional cookie to the original response header.
     * @param res Original response
     * @param name Cookie name
     * @param value Value of it
     * @param attrs Optional attributes, for example "Path=/"
     * @return Head with modified cookie header
     * @throws IOException If it fails
     * @checkstyle ParameterNumberCheck (8 lines)
     */
    private static Iterable<String> extend(final Response res,
        final CharSequence name, final CharSequence value,
        final CharSequence... attrs) throws IOException {
        final String cookie = RsWithCookie.make(
            RsWithCookie.previousValue(res),
            name, value, attrs
        );
        Response resp = res;
        resp = new RsWithHeader(
            new RsWithoutHeader(res, RsWithCookie.SET_COOKIE),
            RsWithCookie.SET_COOKIE,
            cookie
        );
        return resp.head();
    }

    /**
     * Build cookie string.
     * @param previous Previous Cookie value.
     * @param name Cookie name
     * @param value Value of it
     * @param attrs Optional attributes, for example "Path=/"
     * @return Text
     * @throws IOException If it fails
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    private static String make(final String previous, final CharSequence name,
        final CharSequence value,
        final CharSequence... attrs) throws IOException {
        final StringBuilder text = new StringBuilder();
        if (!previous.isEmpty()) {
            text.append(String.format("%s,", previous));
        }
        text.append(String.format("%s=%s", name, value));
        for (final CharSequence attr : attrs) {
            text.append(';').append(attr);
        }
        return text.toString();
    }

    /**
     * Retrieve potential previous cookie string.
     * @param res Current Response
     * @return Current cookie value or empty string.
     * @throws IOException If it fails.
     */
    private static String previousValue(final Response res) throws IOException {
        final StringBuilder cookie = new StringBuilder();
        final StringBuilder value = new StringBuilder();
        for (final String header : res.head()) {
            if (header.contains(RsWithCookie.SET_COOKIE)) {
                cookie.append(header);
                break;
            }
        }
        final Matcher matcher = RsWithCookie.COOKIE_PTRN
            .matcher(cookie.toString());
        if (matcher.find()) {
            value.append(matcher.group(1));
        }
        return value.toString();
    }

    /**
     * Checks value according RFC 6265 section 4.1.1.
     * @param value Cookie value
     * @return Cookie value
     */
    private static CharSequence checkValue(final CharSequence value) {
        if (!RsWithCookie.CVALUE_PTRN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                String.format(
                    "Cookie value %s contains invalid characters",
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
    private static CharSequence checkName(final CharSequence name) {
        if (!RsWithCookie.CNAME_PTRN.matcher(name).matches()) {
            throw new IllegalArgumentException(
                String.format(
                    "Cookie name %s contains invalid characters",
                    name
                )
            );
        }
        return name;
    }
}
