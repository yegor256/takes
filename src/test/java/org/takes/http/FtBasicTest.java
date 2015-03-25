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
package org.takes.http;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;
import org.takes.Take;
import org.takes.Takes;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TsFork;
import org.takes.rq.RqPrint;
import org.takes.tk.TkText;
import org.takes.ts.TsFailure;

/**
 * Test case for {@link FtBasic}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class FtBasicTest {

    /**
     * FtBasic can work.
     * @throws Exception If some problem inside
     */
    @Test
    public void justWorks() throws Exception {
        new FtRemote(new TsFork(new FkRegex("/", "hello, world!"))).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    new JdkRequest(home)
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .assertBody(Matchers.startsWith("hello"));
                }
            }
        );
    }

    /**
     * FtBasic can work with a broken back.
     * @throws Exception If some problem inside
     */
    @Test
    public void gracefullyHandlesBrokenBack() throws Exception {
        new FtRemote(new TsFailure("Jeffrey Lebowski")).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    new JdkRequest(home)
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_INTERNAL_ERROR)
                        .assertBody(Matchers.containsString("Lebowski"));
                }
            }
        );
    }

    /**
     * FtBasic can properly parse incoming HTTP request.
     * @throws Exception If some problem inside
     */
    @Test
    public void parsesIncomingHttpRequest() throws Exception {
        final Takes takes = new Takes() {
            @Override
            public Take route(final Request request) throws IOException {
                MatcherAssert.assertThat(
                    new RqPrint(request).printBody(),
                    Matchers.containsString("Jeff")
                );
                return new TkText("works!");
            }
        };
        new FtRemote(takes).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    new JdkRequest(home)
                        .method("POST")
                        .body().set("Jeff, how are you?").back()
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK);
                }
            }
        );
    }

    /**
     * FtBasic can work with a stuck back.
     * @throws Exception If some problem inside
     */
    @Test
    public void gracefullyHandlesStuckBack() throws Exception {
        final Takes takes = new Takes() {
            @Override
            public Take route(final Request request) throws IOException {
                return new TkText(
                    String.format(
                        "first: %s, second: %s",
                        new RqPrint(request).printBody(),
                        new RqPrint(request).printBody()
                    )
                );
            }
        };
        new FtRemote(takes).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    new JdkRequest(home)
                        .method("POST")
                        .header("Content-Length", "4")
                        .fetch(new ByteArrayInputStream("ddgg".getBytes()))
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .assertBody(Matchers.containsString("second: dd"));
                }
            }
        );
    }

}
