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

import lombok.EqualsAndHashCode;
import org.takes.Response;

/**
 * Response decorator, with an additional cookie.
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
     * Ctor.
     * @param name Cookie name
     * @param value Value of it
     * @param attrs Optional attributes, for example "Path=/"
     */
    public RsWithCookie(final String name, final String value,
        final String... attrs) {
        this(new RsEmpty(), name, value, attrs);
    }

    /**
     * Ctor.
     * @param res Original response
     * @param name Cookie name
     * @param value Value of it
     * @param attrs Optional attributes, for example "Path=/"
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public RsWithCookie(final Response res, final String name,
        final String value, final String... attrs) {
        super(
            new RsWithHeader(
                res, "Set-Cookie", RsWithCookie.make(name, value, attrs)
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
    private static String make(final String name,
        final String value, final String... attrs) {
		if (name.matches("[^\\p{Print}]") || value.matches("[^\\p{Print}]")) {
    		throw new RuntimeException("Cookie name/value cannot contain unprintable characters");
    	}
        final StringBuilder text = new StringBuilder(
            String.format("%s=%s", name, value)
        );
        for (final String attr : attrs) {
            text.append(';').append(attr);
        }
        return text.toString();
    }

}
