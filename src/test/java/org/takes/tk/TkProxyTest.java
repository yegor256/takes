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
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeaders;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkProxy}.
 * The test verify the different HTTP methods (GET, POST, etc),
 * as well as the different combinations of request/response headers.
 * @author Dragan Bozanovic (bozanovicdr@gmail.com)
 * @version $Id$
 * @since 0.25
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class TkProxyTest {

    /**
     * TkProxy can work.
     * @throws Exception If some problem inside
     */
    @Test
    public void justWorks() throws Exception {
        final String testText = "Hello, world!";
        new FtRemote(new TkFixed(testText)).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        new RsPrint(
                            new TkProxy(
                                TkProxyTest.this.formatProxyHost(home)
                            ).act(new RqFake())
                        ).print(),
                        Matchers.containsString(testText)
                    );
                }
            }
        );
    }

    /**
     * TkProxy can work with GET HTTP method
     * Plus 4 random headers checked.
     * @throws Exception If some problem inside
     */
    @Test
    public void actsOnHttpGETWithRequestHeaders() throws Exception {
        final String testText = "GETTest";
        new FtRemote(new TkFixed(testText)).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        TkProxyTest.this.actResponse(
                            home,
                            javax.ws.rs.HttpMethod.GET,
                            "TestHeader: getTest",
                            "SomeHeader: getTestValue",
                            "Content-Length: 130",
                            "Transfer-Encoding: get"
                        ),
                        Matchers.containsString(testText)
                    );
                }
            }
        );
    }

    /**
     * TkProxy can work with POST HTTP method
     * Plus 4 random headers checked.
     * @throws Exception If some problem inside
     */
    @Test
    public void actsOnHttpPOSTWithRequestHeaders() throws Exception {
        final String testText = "POSTTest";
        new FtRemote(new TkFixed(testText)).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        TkProxyTest.this.actResponse(
                            home,
                            javax.ws.rs.HttpMethod.POST,
                            "TestHeader: PostTest",
                            "SomeHeader: PostTestValue",
                            "Content-Length: 131",
                            "Transfer-Encoding: Post"
                        ),
                        Matchers.containsString(testText)
                    );
                }
            }
        );
    }

    /**
     * TkProxy can work with PUT HTTP method
     * Plus 4 random headers checked.
     * @throws Exception If some problem inside
     */
    @Test
    public void actsOnHttpPUTWithRequestHeaders() throws Exception {
        final String testText = "PUTTest";
        new FtRemote(new TkFixed(testText)).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        TkProxyTest.this.actResponse(
                            home,
                            javax.ws.rs.HttpMethod.PUT,
                            "TestHeader: PUTTest",
                            "SomeHeader: PUTTestValue",
                            "Content-Length: 132",
                            "Transfer-Encoding: PUT"
                        ),
                        Matchers.containsString(testText)
                    );
                }
            }
        );
    }

    /**
     * TkProxy can work with DELETE HTTP method
     * Plus 4 random headers checked.
     * @throws Exception If some problem inside
     */
    @Test
    public void actsOnHttpDELETEWithRequestHeaders() throws Exception {
        final String testText = "DELETETest";
        new FtRemote(new TkFixed(testText)).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        TkProxyTest.this.actResponse(
                            home,
                            javax.ws.rs.HttpMethod.DELETE,
                            "TestHeader: DELETETest",
                            "SomeHeader: DELETETestValue",
                            "Content-Length: 133",
                            "Transfer-Encoding: DELETE"
                        ),
                        Matchers.containsString(testText)
                    );
                }
            }
        );
    }

    /**
     * TkProxy can work with OPTIONS HTTP method
     * Plus 4 random headers checked.
     * @throws Exception If some problem inside
     */
    @Test
    public void actsOnHttpOPTIONSWithRequestHeaders() throws Exception {
        final String testText = "OPTIONSTest";
        new FtRemote(new TkFixed(testText)).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        TkProxyTest.this.actResponse(
                            home,
                            javax.ws.rs.HttpMethod.OPTIONS,
                            "TestHeader: OPTIONSTest",
                            "SomeHeader: OPTIONSTestValue",
                            "Content-Length: 134",
                            "Transfer-Encoding: OPTIONS"
                        ),
                        Matchers.containsString(testText)
                    );
                }
            }
        );
    }

    /**
     * TkProxy can work with HEAD HTTP method
     * Plus 4 random headers checked.
     * @throws Exception If some problem inside
     */
    @Test
    public void actsOnHttpHEADWithRequestHeaders() throws Exception {
        final String testText = "HEADTest";
        new FtRemote(new TkFixed(testText)).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        TkProxyTest.this.actResponse(
                            home,
                            javax.ws.rs.HttpMethod.HEAD,
                            "TestHeader: HEADTest",
                            "SomeHeader: HEADTestValue",
                            "Content-Length: 135",
                            "Transfer-Encoding: HEAD"
                        ),
                        Matchers.containsString("OK")
                    );
                }
            }
        );
    }
    /**
     * TkProxy can throw IOException on invalid HTTP methods.
     * @throws Exception If some problem inside
     */
    @Test(expected = IOException.class)
    public void actsOnInvalidHttpMethods() throws Exception {
        new FtRemote(new TkFixed("InvalidHeaderTest")).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    TkProxyTest.this.actResponse(
                        home, "INVALIDHTTPMETHOD"
                    );
                }
            }
        );
    }

    /**
     * TkProxy can proxy original response headers.
     * @throws Exception If some problem inside
     */
    @Test
    public void actsOnResponseHeaders() throws Exception {
        final String testString = "TestRH";
        new FtRemote(
                new TkFixed(testString)
        ).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        new RsPrint(
                            new TkProxy(
                                TkProxyTest.this.formatProxyHost(home)
                            ).act(new RqFake())
                        ).print(),
                            Matchers.allOf(
                                        Matchers.containsString(testString),
                                        Matchers.containsString(
                                                "X-Takes-TkProxy:"
                                        )
                                )
                        );
                    }
                }
        );
    }

    /**
     * Performs proxy.act() and gets String of the response.
     * @param home Initial URL
     * @param method HTTP method to run
     * @param headers HTTP headers to use
     * @return String of the response received
     * @throws IOException if something goes wrong
     */
    private String actResponse(
            final URI home, final String method, final String... headers
    )
            throws IOException {
        return new RsPrint(new TkProxy(
            TkProxyTest.this.formatProxyHost(home)
                ).act(
                        new RqWithHeaders(
                        new RqFake(
                                method, "/"
                        ), headers
                        )
                )
        ).print();
    }

    /**
     * Formats the string for the proxy host.
     *
     * @param home Initial host
     * @return Proxy redefined host string
     */
    private String formatProxyHost(final URI home) {
        return String.format(
            "%s:%d", home.getHost(), home.getPort()
        );
    }
}
