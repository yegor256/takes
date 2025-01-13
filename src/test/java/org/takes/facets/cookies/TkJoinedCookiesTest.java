/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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

import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;
import org.takes.rs.RsWithHeaders;
import org.takes.tk.TkFixed;

/**
 * Test case for {@link TkJoinedCookies}.
 *
 * @since 0.11
 * @checkstyle ClassDataAbstractionCouplingCheck (50 lines)
 */
final class TkJoinedCookiesTest {

    @Test
    void joinsCookies() throws Exception {
        new Assertion<>(
            "Response with joined cookies",
            new RsPrint(
                new TkJoinedCookies(
                    new TkFixed(
                        new RsWithHeaders(
                            new RsText(),
                            "Set-Cookie: a=1",
                            "Set-cookie: b=1; Path=/"
                        )
                    )
                ).act(new RqFake())
            ),
            new HasString("Set-Cookie: a=1, b=1; Path=/")
        ).affirm();
    }

}
