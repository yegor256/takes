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
package org.takes.tk;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.FkMethods;
import org.takes.facets.fork.TkFork;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqHref;
import org.takes.rq.RqMethod;
import org.takes.rq.RqPrint;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;

/**
 * Test case for {@link TkProxy}.
 * @author Dragan Bozanovic (bozanovicdr@gmail.com)
 * @version $Id$
 * @since 0.25
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@RunWith(Parameterized.class)
@SuppressWarnings("PMD.TooManyMethods")
public final class TkProxyTest {

    /**
     * A {@code Take} implementation that returns the content of the request
     * as body of its response.
     */
    private static final Take ECHO = new Take() {
        @Override
        public Response act(final Request req) throws IOException {
            return new RsText(new RqPrint(req).print());
        }
    };

    /**
     * Http method.
     */
    private final transient String method;

    /**
     * Expected test result.
     */
    private final transient String expected;

    /**
     * Constructor.
     * @param method Http method.
     * @param expected Expected test result.
     */
    public TkProxyTest(final String method, final String expected) {
        this.method = method;
        this.expected = expected;
    }

    /**
     * Http methods for testing.
     * @return The testing data
     */
    @Parameterized.Parameters
    public static Collection<Object[]> methods() {
        return Arrays.asList(
            new Object[][]{
                {RqMethod.POST, "hello, post!"},
                {RqMethod.GET, "hello, get!"},
                {RqMethod.PUT, "hello, put!"},
                {RqMethod.DELETE, "hello, delete!"},
                {RqMethod.TRACE, "hello, trace!"},
            });
    }

    /**
     * TkProxy can just work.
     * @throws Exception If some problem inside
     */
    @Test
    public void justWorks() throws Exception {
        new FtRemote(
            new TkFork(
                new FkMethods(this.method, new TkFixed(this.expected))
            )
        ).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        new RsPrint(
                            new TkProxy(home).act(
                                new RqFake(TkProxyTest.this.method)
                            )
                        ).print(),
                        Matchers.containsString(
                            TkProxyTest.this.expected
                        )
                    );
                }
            }
        );
    }

    /**
     * TkProxy can correctly maps path string.
     * @throws Exception If some problem inside
     * @checkstyle AnonInnerLengthCheck (100 lines)
     */
    @Test
    public void correctlyMapsPathString() throws Exception {
        final Take take = new Take() {
            @Override
            public Response act(final Request req) throws IOException {
                return new RsText(new RqHref.Base(req).href().toString());
            }
        };
        new FtRemote(
            new TkFork(
                new FkMethods(this.method, take)
            )
        ).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        new RsPrint(
                            new TkProxy(home).act(
                                new RqFake(
                                    TkProxyTest.this.method,
                                    "/a/%D0%B0/c?%D0%B0=1#%D0%B0"
                                )
                            )
                        ).printBody(),
                        Matchers.equalTo(
                            String.format(
                                "http://%s:%d/a/%%D0%%B0/c?%%D0%%B0=1",
                                home.getHost(), home.getPort()
                            )
                        )
                    );
                }
            }
        );
    }

    /**
     * TkProxy can properly modifies the host header.
     * @throws Exception If some problem inside
     */
    @Test
    public void modifiesHost() throws Exception {
        new FtRemote(
            new TkFork(
                new FkMethods(this.method, TkProxyTest.ECHO)
            )
        ).exec(
            // @checkstyle AnonInnerLengthCheck (100 lines)
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        new RsPrint(new TkProxy(
                            home.toURL().toString()
                        ).act(
                            new RqFake(
                                Arrays.asList(
                                    String.format(
                                        "%s /f?%%D0%%B0=3&b-6",
                                        TkProxyTest.this.method
                                    ),
                                    "Host: example.com",
                                    "Accept: text/xml",
                                    "Accept: text/html"
                                ),
                                ""
                            )
                        )).printBody(),
                        Matchers.containsString(
                            String.format(
                                "Host: %s:%d",
                                home.getHost(),
                                home.getPort()
                            )
                        )
                    );
                }
            }
        );
    }

    /**
     * TkProxy can add its specific header.
     * @throws Exception If some problem inside
     */
    @Test
    public void addsSpecificHeader() throws Exception {
        final String mark = "foo";
        new FtRemote(
            new TkFork(
                new FkMethods(this.method, TkProxyTest.ECHO)
            )
        ).exec(
            // @checkstyle AnonInnerLengthCheck (100 lines)
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        new RsPrint(new TkProxy(
                            home.toURL().toString(),
                            mark
                        ).act(
                            new RqFake(
                                Arrays.asList(
                                    String.format(
                                        "%s /%%D0%%B0",
                                        TkProxyTest.this.method
                                    ),
                                    "Host: www.bar.com"
                                ),
                                ""
                            )
                        )).printHead(),
                        Matchers.containsString(
                            String.format(
                                // @checkstyle LineLengthCheck (1 line)
                                "X-Takes-TkProxy: from /%%D0%%B0 to %s/%%D0%%B0 by %s",
                                home,
                                mark
                            )
                        )
                    );
                }
            }
        );
    }

    /**
     * TkProxy can add all initial headers.
     * @throws Exception If some problem inside
     */
    @Test
    public void addsAllInitialHeaders() throws Exception {
        final String body = "Hello World !";
        new FtRemote(
            new TkFork(
                new FkMethods("POST", TkProxyTest.ECHO)
            )
        ).exec(
            // @checkstyle AnonInnerLengthCheck (100 lines)
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        new RsPrint(
                            new TkProxy(home).act(
                                new RqFake(
                                    Arrays.asList(
                                        "POST /%D0%B0",
                                        String.format(
                                            "Content-Length: %s",
                                            body.length()
                                        ),
                                        "Content-Type: text/plain",
                                        "Accept: text/json",
                                        "Cookie: a=45",
                                        "Cookie: ttt=ALPHA",
                                        "Accept-Encoding: gzip",
                                        "Host: www.bar-foo.com"
                                    ),
                                    body
                                )
                            )
                        ).printBody(),
                        Matchers.allOf(
                            Matchers.containsString("Content-Length:"),
                            Matchers.containsString("Content-Type:"),
                            Matchers.containsString("Accept:"),
                            Matchers.containsString("Cookie: a"),
                            Matchers.containsString("Cookie: ttt"),
                            Matchers.containsString("Accept-Encoding:")
                        )
                    );
                }
            }
        );
    }
}
