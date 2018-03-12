/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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
package org.takes.facets.cookies;

import com.google.common.base.Joiner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link RsWithCookie}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.9.6
 */
public final class RsWithCookieTest {

    /**
     * RsWithCookie can add cookies.
     * @throws IOException If some problem inside
     * @checkstyle MultipleStringLiteralsCheck (17 lines)
     */
    @Test
    public void addsCookieToResponse() throws IOException {
        final String foo = "foo";
        final String works = "works?";
        final String path = "Path=/";
        MatcherAssert.assertThat(
            new RsPrint(
                new RsWithCookie(
                    new RsEmpty(),
                    foo,
                    works,
                    path
                )
            ).print(),
            Matchers.equalTo(
                RsWithCookieTest.cookies(
                    RsWithCookieTest.formatCookie(foo, works, path)
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
        final String qux = "qux";
        final String bar = "bar";
        final String value = "value?";
        final String path = "Path=/qux";
        MatcherAssert.assertThat(
            new RsPrint(
                new RsWithCookie(
                    new RsWithCookie(
                        new RsEmpty(),
                        qux,
                        value,
                        path
                    ),
                    bar, "worksToo?", "Path=/2nd/path/"
                )
            ).print(),
            Matchers.equalTo(
                RsWithCookieTest.cookies(
                    RsWithCookieTest.formatCookie(qux, value, path),
                    "bar=worksToo?;Path=/2nd/path/"
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
        new RsWithCookie(new RsEmpty(), "cookiename", "wo\"rks");
    }

    /**
     * Returns the joined cookie.
     * @param cookies Cookies values
     * @return Joined cookie
     */
    private static String cookies(final String... cookies) {
        final List<String> list = new ArrayList<>(cookies.length + 3);
        list.add("HTTP/1.1 200 OK");
        for (final String cookie : cookies) {
            list.add(String.format("Set-Cookie: %s;", cookie));
        }
        list.add("");
        list.add("");
        return Joiner.on("\r\n").join(list.iterator());
    }

    /**
     * Returns the formatted cookie.
     * @param name Cookie name
     * @param value Cookie value
     * @param path Cookie path
     * @return Formatted cookie value
     */
    private static String formatCookie(final String name, final String value,
        final String path) {
        return String.format("%s=%s;%s", name, value, path);
    }
}
