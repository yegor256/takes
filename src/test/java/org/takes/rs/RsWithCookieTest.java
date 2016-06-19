/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Yegor Bugayenko
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

import com.google.common.base.Joiner;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RsWithCookie}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.9.6
 */
public final class RsWithCookieTest {

    /**
     * Carriage return constant.
     */
    private static final String CRLF = "\r\n";
    /**
     * Bar.
     */
    private static final String BAR = "bar";
    /**
     * Http ok.
     */
    private static final String HTTP_OK = "HTTP/1.1 200 OK";
    /**
     * Works.
     */
    private static final String WORKS = "works?";
    /**
     * Set cookie.
     */
    private static final String SET_COOKIE = "Set-Cookie: foo=works?;Path=/;";
    /**
     * Foo.
     */
    private static final String FOO = "foo";
    /**
     * Path.
     */
    private static final String PATH = "Path=/";

    /**
     * RsWithCookie can add cookies.
     * @throws IOException If some problem inside
     * @checkstyle MultipleStringLiteralsCheck (17 lines)
     */
    @Test
    public void addsCookieToResponse() throws IOException {
        MatcherAssert.assertThat(
            new RsPrint(
                new RsWithCookie(
                    new RsEmpty(),
                    RsWithCookieTest.FOO, 
                    RsWithCookieTest.WORKS, 
                    RsWithCookieTest.PATH
                )
            ).print(),
            Matchers.equalTo(
                Joiner.on(RsWithCookieTest.CRLF).join(
                    RsWithCookieTest.HTTP_OK,
                    RsWithCookieTest.SET_COOKIE,
                    "",
                    ""
                )
            )
        );
    }

    /**
     * RsWithCookie can add several cookies (with several decorations).
     * @throws IOException If some problem inside
     * @checkstyle MultipleStringLiteralsCheck (17 lines)
     */
    @Test
    public void addsMultipleCookies() throws IOException {
        MatcherAssert.assertThat(
            new RsPrint(
                new RsWithCookie(
                    new RsWithCookie(
                        new RsEmpty(),
                        RsWithCookieTest.FOO, 
                        RsWithCookieTest.WORKS, 
                        RsWithCookieTest.PATH
                    ),
                    RsWithCookieTest.BAR, "worksToo?", "Path=/2nd/path/"
                )
            ).print(),
            Matchers.equalTo(
                Joiner.on(RsWithCookieTest.CRLF).join(
                    RsWithCookieTest.HTTP_OK,
                    RsWithCookieTest.SET_COOKIE,
                    "Set-Cookie: bar=worksToo?;Path=/2nd/path/;",
                    "",
                    ""
                )
            )
        );
    }

    /**
     * RsWithCookie can reject invalid cookie name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidName() {
        new RsWithCookie(new RsEmpty(), "f oo", "works");
    }

    /**
     * RsWithCookie can reject invalid cookie value.
     */
    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidValue() {
        new RsWithCookie(new RsEmpty(), RsWithCookieTest.BAR, "wo\"rks");
    }
}
