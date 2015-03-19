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
package org.takes.facets.fallback;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;
import org.takes.Take;
import org.takes.Takes;
import org.takes.facets.fork.TsFork;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqMethod;
import org.takes.rs.RsPrint;
import org.takes.tk.TkHTML;
import org.takes.tk.TkText;

/**
 * Test case for {@link org.takes.facets.fallback.TsFallback}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class TsFallbackTest {

    /**
     * TsFallback can fall back.
     * @throws IOException If some problem inside
     */
    @Test
    public void fallsBack() throws IOException {
        MatcherAssert.assertThat(
            new RsPrint(
                new TsFallback(
                    new Takes() {
                        @Override
                        public Take route(final Request request) {
                            throw new UnsupportedOperationException("#take()");
                        }
                    },
                    new TkText("an exception, sorry")
                ).route(new RqFake(RqMethod.GET, "/", "")).act()
            ).print(),
            Matchers.startsWith("HTTP/1.1 200 OK")
        );
    }

    /**
     * TsFallback can fall back many times.
     * @throws IOException If some problem inside
     * @since 0.10
     */
    @Test
    public void fallsBackManyTimes() throws IOException {
        final Takes takes = new TsFallback(
            new TsFork(),
            new TkHTML("oops, nothing here")
        );
        final FtRemote.Script script = new FtRemote.Script() {
            @Override
            public void exec(final URI home) throws IOException {
                new JdkRequest(home)
                    .uri().path("/absent-resource").back()
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_OK)
                    .assertBody(Matchers.startsWith("oops"));
            }
        };
        new FtRemote(takes).exec(script);
        new FtRemote(takes).exec(script);
    }

}
