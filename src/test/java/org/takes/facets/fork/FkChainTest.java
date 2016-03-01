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
package org.takes.facets.fork;

import java.io.IOException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

import com.google.common.base.Joiner;

/**
 * Test case for {@link FkChain}.
 * @author Carlos Ginés (efrel.v2@gmail.com)
 * @version $Id$
 * @since 0.32
 */
public final class FkChainTest {

    /**
     * TkFork can return empty {@code Opt<T>} if no fork accepts the request.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnsEmptyIfNoForkAcceptsRequest() throws IOException {
        final String body = "hello test!";
        MatcherAssert.assertThat(
            new FkChain(new FkRegex("/h[a-z]{2}", body)).route(
                new RqFake("GET", "/rasengan", "")
            ).has(),
            Matchers.equalTo(false)
        );
    }
    /**
     * TkFork can dispatch by regular expression.
     * @throws IOException If some problem inside
     */
    @Test
    public void dispatchesByRegularExpression() throws IOException {
        final String body1 = "hello test!";
        final String body2 = "how do you do the things that you do?";
        MatcherAssert.assertThat(
            new RsPrint(
                new FkChain(
                	new FkRegex("/g[a-z]{2}", body1),
                	new FkRegex("/h[a-z]{2}", body2),
                	new FkRegex("/i[a-z]{2}", body1)
                ).route(new RqFake("GET", "/hey?yu", "")).get()
            ).print(),
            Matchers.equalTo(
                Joiner.on("\r\n").join(
                    "HTTP/1.1 200 OK",
                    String.format("Content-Length: %s", body2.length()),
                    "Content-Type: text/plain",
                    "",
                    body2
                )
            )
        );
    }
}
