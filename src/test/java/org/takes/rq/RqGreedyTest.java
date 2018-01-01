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
package org.takes.rq;

import com.google.common.base.Joiner;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;

/**
 * Test case for {@link RqGreedy}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.16
 */
public final class RqGreedyTest {

    /**
     * RqGreedy can make request greedy.
     * @throws IOException If some problem inside
     */
    @Test
    public void makesRequestGreedy() throws IOException {
        final String body = Joiner.on("\r\n").join(
            "GET /test HTTP/1.1",
            "Host: localhost",
            "",
            "... the body ..."
        );
        final Request req = new RqGreedy(
            new RqWithHeader(
                new RqLive(
                    new ByteArrayInputStream(
                        body.getBytes()
                    )
                ),
                "Content-Length",
                String.valueOf(body.getBytes().length)
            )
        );
        MatcherAssert.assertThat(
            new RqPrint(req).printBody(),
            Matchers.containsString("the body")
        );
        MatcherAssert.assertThat(
            new RqPrint(req).printBody(),
            Matchers.containsString("the body ...")
        );
    }

}
