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
package org.takes.rq;

import com.google.common.base.Joiner;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RqWithDefaultHeader}.
 * @author Andrey Eliseev (aeg.exper0@gmail.com)
 * @version $Id$
 * @since 0.31
 */
public final class RqWithDefaultHeaderTest {

    /**
     * Carriage return constant.
     */
    private static final String CRLF = "\r\n";

    /**
     * RqWithDefaultHeader can provide default header value.
     * @throws IOException If some problem inside
     */
    @Test
    public void providesDefaultHeader() throws IOException {
        MatcherAssert.assertThat(
            new RqPrint(
                new RqWithDefaultHeader(
                    new RqFake(),
                    "X-Default-Header1",
                    "X-Default-Value1"
                )
            ).print(),
            Matchers.startsWith(
                Joiner.on(RqWithDefaultHeaderTest.CRLF).join(
                    // @checkstyle MultipleStringLiteralsCheck (2 lines)
                    "GET /",
                    "Host: www.example.com",
                    "X-Default-Header1: X-Default-Value"
                )
            )
        );
    }

    /**
     * RqWithDefaultHeader can override default value.
     * @throws IOException If some problem inside
     */
    @Test
    public void allowsOverrideDefaultHeader() throws IOException {
        MatcherAssert.assertThat(
            new RqPrint(
                new RqWithDefaultHeader(
                    new RqWithHeader(
                        new RqFake(),
                        // @checkstyle MultipleStringLiteralsCheck (1 lines)
                        "X-Default-Header2",
                        "Non-Default-Value2"
                    ),
                    "X-Default-Header2",
                    "X-Default-Value"
                )
            ).print(),
            Matchers.startsWith(
                Joiner.on(RqWithDefaultHeaderTest.CRLF).join(
                    "GET /",
                    "Host: www.example.com",
                    "X-Default-Header2: Non-Default-Value"
                )
            )
        );
    }
}
