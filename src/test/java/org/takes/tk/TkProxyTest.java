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
package org.takes.tk;

import java.io.IOException;
import java.net.URI;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqHref;
import org.takes.rq.RqMethod;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;

/**
 * Test case for {@link TkProxy}.
 * @author Dragan Bozanovic (bozanovicdr@gmail.com)
 * @version $Id$
 * @since 0.25
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @todo #377:30min/DEV We need more tests for TkProxy.
 *  The tests should verify the different HTTP methods (GET, POST, etc),
 *  as well as the different combinations of request/response headers.
 *
 * @todo #458:30min We need to support the http method CONNECT.
 *  Currently, the use of Connect throws an exception.
 */
public final class TkProxyTest {

    /**
     * An array of http methods for testing.
     */
    private static final String[] METHODS = {
        RqMethod.POST,
        RqMethod.GET,
        RqMethod.PUT,
        RqMethod.DELETE,
        RqMethod.TRACE,
    };

    /**
     * The expected result for the Head http request.
     */
    private static final String RESULT = "";

    /**
     * TkProxy can just work.
     * @throws Exception If some problem inside
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    @Test
    public void justWorks() throws Exception {
        for (final String method:TkProxyTest.METHODS) {
            new FtRemote(new TkFixed("hello, world!")).exec(
                new FtRemote.Script() {
                    @Override
                    public void exec(final URI home) throws IOException {
                        MatcherAssert.assertThat(
                            new RsPrint(
                                new TkProxy(home).act(new RqFake(method))
                            ).print(),
                            Matchers.containsString("hello")
                        );
                    }
                }
            );
        }
    }

    /**
     * TkProxy can just work on Head.
     * @throws Exception If some problem inside
     */
    @Test
    public void justWorksOnHEAD() throws Exception {
        new FtRemote(new TkFixed("Head test!")).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        new RsPrint(
                            new TkProxy(home).act(
                                new RqFake(RqMethod.HEAD)
                            )
                        ).print(),
                        Matchers.containsString(TkProxyTest.RESULT)
                    );
                }
            }
        );
    }

    /**
     * TkProxy can correctly maps path string.
     * @throws Exception If some problem inside
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    @Test
    public void correctlyMapsPathString() throws Exception {
        final Take take = new Take() {
            @Override
            public Response act(final Request req) throws IOException {
                return new RsText(new RqHref.Base(req).href().toString());
            }
        };
        for (final String method:TkProxyTest.METHODS) {
            new FtRemote(take).exec(
                new FtRemote.Script() {
                    @Override
                    public void exec(final URI home) throws IOException {
                        MatcherAssert.assertThat(
                            new RsPrint(
                                new TkProxy(home).act(
                                    new RqFake(method, "/a/b/c")
                                )
                            ).printBody(),
                            Matchers.equalTo(
                                String.format(
                                    "http://%s:%d/a/b/c",
                                    home.getHost(), home.getPort()
                                )
                            )
                        );
                    }
                }
            );
        }
    }

    /**
     * TkProxy can correctly maps path string on head.
     * @throws Exception If some problem inside
     */
    @Test
    public void correctlyMapsPathStringOnHead() throws Exception {
        final Take take = new Take() {
            @Override
            public Response act(final Request req) throws IOException {
                return new RsText(new RqHref.Base(req).href().toString());
            }
        };
        new FtRemote(take).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        new RsPrint(
                            new TkProxy(home).act(
                                new RqFake(RqMethod.HEAD, "Head")
                            )
                        ).printBody(),
                        Matchers.equalTo(
                            String.format(
                                TkProxyTest.RESULT,
                                home.getHost(),
                                home.getPort()
                            )
                        )
                    );
                }
            }
        );
    }
}
