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
public final class TkProxyTest {

    /**
     * Test string.
     */
    private static final String HELLO_WORLD = "hello, world!";

    /**
     * Format string.
     */
    private static final String FORMAT = "%s:%d";

    /**
     * Dash constant.
     */
    private static final String DASH = "/";

    /**
     * Response string.
     */
    private static final String HEADER = "X-Takes-TkProxy:";

    /**
     * OK response.
     */
    private static final String OK = "HTTP/1.1 200 OK";

    /**
     * TkProxy can work with different HTTP methods
     * (GET, POST, OPTIONS, PUT, DELETE).
     * Plus 4 random headers checked.
     * TkProxy can throw IOException on invalid HTTP methods.
     * @throws Exception If some problem inside
     */
    @Test
    public void actsOnHttpMethodsWithRequestHeaders() throws Exception {
        final String[] headers = {
            "TestHeader: someValue",
            "SomeHeader: testValue",
            "Content-Length: 130",
            "Transfer-Encoding: blah",
        };
        new FtRemote(new TkFixed(HELLO_WORLD)).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        TkProxyTest.this.actResponse(home, "GET", headers),
                                    Matchers.startsWith(OK)
                    );
                }
            }
        );
        new FtRemote(new TkFixed(HELLO_WORLD)).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        TkProxyTest.this.actResponse(home, "POST", headers),
                        Matchers.startsWith(OK)
                    );
                }
            }
        );
        new FtRemote(new TkFixed(HELLO_WORLD)).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        TkProxyTest.this.actResponse(home, "OPTIONS", headers),
                        Matchers.startsWith(OK)
                    );
                }
            }
        );
        new FtRemote(new TkFixed(HELLO_WORLD)).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        TkProxyTest.this.actResponse(home, "PUT", headers),
                        Matchers.startsWith(OK)
                    );
                }
            }
        );
        new FtRemote(new TkFixed(HELLO_WORLD)).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        TkProxyTest.this.actResponse(home, "DELETE", headers),
                        Matchers.startsWith(OK)
                    );
                }
            }
        );
        new FtRemote(new TkFixed(HELLO_WORLD)).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    try {
                        TkProxyTest.this.actResponse(home, "INVALID", headers);
                        MatcherAssert.assertThat("IOException expected", false);
                    } catch (final IOException exception) {
                        MatcherAssert.assertThat("IOException - OK", true);
                    }
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
        new FtRemote(
                new TkFixed(
                        HELLO_WORLD
                )
        ).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        new RsPrint(
                            new TkProxy(
                                String.format(
                                    FORMAT, home.getHost(), home.getPort()
                                    )
                            ).act(new RqFake())
                        ).print(),
                            Matchers.allOf(
                                        Matchers.containsString(HELLO_WORLD),
                                        Matchers.containsString(
                                                HEADER
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
                        String.format(
                                FORMAT, home.getHost(),
                                home.getPort()
                        )
                ).act(
                        new RqWithHeaders(
                        new RqFake(
                                method, DASH
                        ), headers
                        )
                )
        ).print();
    }
}
