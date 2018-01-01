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
import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.HttpException;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsJson;
import org.takes.rs.RsPrint;
import org.takes.tk.TkEmpty;
import org.takes.tk.TkFixed;

/**
 * Test case for {@link TkProduces}.
 * @author Eugene Kondrashev (eugene.kondrashev@gmail.com)
 * @version $Id$
 * @since 0.14
 */
public final class TkProducesTest {

    /**
     * TkProduces can fail on unsupported Accept header.
     * @throws IOException If some problem inside
     */
    @Test(expected = HttpException.class)
    public void failsOnUnsupportedAcceptHeader() throws IOException {
        final Take produces = new TkProduces(
            new TkEmpty(),
            "text/json,application/json"
        );
        produces.act(
            new RqFake(
                Arrays.asList(
                    "GET /hz0",
                    "Host: as0.example.com",
                    "Accept: text/xml"
                ),
                ""
            )
        ).head();
    }

    /**
     * TkProduce can produce correct type response.
     * @throws IOException If some problem inside
     */
    @Test
    public void producesCorrectContentTypeResponse() throws IOException {
        final Take produces = new TkProduces(
            new TkFixed(new RsJson(new RsEmpty())),
            "text/json"
        );
        final Response response = produces.act(
            new RqFake(
                Arrays.asList(
                    "GET /hz09",
                    "Host: as.example.com",
                    "Accept: text/json"
                ),
                ""
            )
        );
        MatcherAssert.assertThat(
            new RsPrint(response).print(),
            Matchers.equalTo(
                Joiner.on("\r\n").join(
                    "HTTP/1.1 200 OK",
                    "Content-Type: application/json",
                    "",
                    ""
                )
            )
        );
    }
}
