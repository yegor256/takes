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
package org.takes.facets.fork;

import com.google.common.base.Joiner;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link FkChain}.
 * @author Carlos Gines (efrel.v2@gmail.com)
 * @version $Id$
 * @since 0.33
 */
public final class FkChainTest {

    /**
     * FkChain can gracefully work when no fork matches the request.
     * @throws Exception If some problem inside
     */
    @Test
    public void gracefullyHandlesNoForkMatching() throws Exception {
        MatcherAssert.assertThat(
            new FkChain(
                new FkRegex("/doyoumatch?", "Hello. It's me."),
                new FkRegex("/plzmatch!", "I am your father")
            ).route(new RqFake("POST", "/idontmatch")).has(),
            Matchers.equalTo(false)
        );
    }

    /**
     * FkChain can dispatch by regular expression.
     * @throws Exception If some problem inside
     */
    @Test
    public void dispatchesByRegularExpression() throws Exception {
        final String body = "hello test!";
        MatcherAssert.assertThat(
            new RsPrint(
                new FkChain(
                    new FkRegex("/g[a-z]{2}", ""),
                    new FkRegex("/h[a-z]{2}", body),
                    new FkRegex("/i[a-z]{2}", "")
                ).route(new RqFake("GET", "/hey?yu")).get()
            ).print(),
            Matchers.equalTo(
                Joiner.on("\r\n").join(
                    "HTTP/1.1 200 OK",
                    String.format("Content-Length: %s", body.length()),
                    "Content-Type: text/plain",
                    "",
                    body
                )
            )
        );
    }
}
