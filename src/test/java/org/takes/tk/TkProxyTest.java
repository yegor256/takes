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
import com.jcabi.http.Request;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeaders;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkProxy}.
 * @author Dragan Bozanovic (bozanovicdr@gmail.com)
 * @version $Id$
 * @since 0.25
 *
 *  The tests should verify the different HTTP methods (GET, POST, etc),
 *  as well as the different combinations of request/response headers.
 */
public final class TkProxyTest {

    /* Test string. */
    public static final String HELLO_WORLD = "hello, world!";

    /* Format string. */
    public static final String FORMAT = "%s:%d";

    /* Dash constant. */
    public static final String DASH = "/";
    public static final String X_TAKES_TK_PROXY_FROM = "X-Takes-TkProxy: from ";

    /**
     * TkProxy can work.
     * @throws Exception If some problem inside
     */
    @Test
    public void justWorks() throws Exception {
        new FtRemote(new TkFixed(HELLO_WORLD)).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        new RsPrint(
                            new TkProxy(
                                    String.format(
                                            FORMAT,
                                            home.getHost(), home.getPort()
                                    )
                            ).act(new RqFake())
                        ).print(),
                        Matchers.containsString(HELLO_WORLD)
                    );
                }
            }
        );
    }

    /**
     * Verifies TkProxy against HTTP methods (GET, POST, OPTIONS, PUT, DELETE).
     *
     * @throws Exception If some problem inside
     */
    @Test
    public void actsOnHttpMethods() throws Exception {
        for (final String validHttMethod : new String[]{Request.GET,
                Request.POST, Request.OPTIONS, Request.PUT, Request.DELETE,})
        {
            this.acts(validHttMethod);
        }
    }

    /**
     * Verifies TkProxy against HEAD HTTP method (no response body).
     *
     * @throws Exception If some problem inside
     */
    @Test
    public void actsOnHEAD() throws Exception {
        new FtRemote(new TkFixed(HELLO_WORLD)).exec(
                new FtRemote.Script() {
                    @Override
                    public void exec(final URI home) throws IOException {
                        MatcherAssert.assertThat(
                                new RsPrint(
                                        new TkProxy(
                                                String.format(
                                                        FORMAT,
                                                        home.getHost(), home.getPort()
                                                )
                                        ).act(new RqWithHeaders(
                                                new RqFake("HEAD", DASH)))
                                ).print(),
                                Matchers.startsWith("HTTP/1.1 200 OK")
                        );
                    }
                }
        );
    }

    /**
     * Verifies TkProxy against invalid HTTP methods
     * (verifies IO Exception is thrown).
     *
     * @throws Exception If some problem inside
     */
    @Test(expected = IOException.class)
    public void actsOnInvalidHttpMethods() throws Exception {
        this.acts("INVALIDHTTPMETHOD");
    }

    /**
     * Verifies TkProxy against request headers
     * (that the original request has been proxied).
     * (4 random headers)
     *
     * @author Aygul Schworer (aygul.schworer@gmail.com)
     *
     * @throws Exception If some problem inside
     */
    @Test
    public void actsOnReqHeaders() throws Exception {
        this.acts("GET",
                "TestHeader: someValue",
                "SomeHeader: testValue",
                "Content-Length: 130",
                "Transfer-Encoding: blah");
    }

    /**
     * Verifies TkProxy against response headers
     * (that the original response has been proxied).
     *
     * @throws Exception If some problem inside
     */
    @Test
    public void actsOnResHeaders() throws Exception {
        new FtRemote(new TkFixed(HELLO_WORLD)).exec(
                new FtRemote.Script() {
                    @Override
                    public void exec(final URI home) throws IOException {
                        MatcherAssert.assertThat(
                                new RsPrint(
                                    new TkProxy(
                                        String.format(
                                                FORMAT,
                                                home.getHost(), home.getPort()
                                        )
                                    ).act(new RqFake())
                                ).print(),
                                Matchers.allOf(
                                        Matchers.containsString(HELLO_WORLD),
                                        Matchers.containsString
                                                (X_TAKES_TK_PROXY_FROM)
                                )
                        );
                    }
                }
        );
    }

    /**
     * Acts.
     *
     * @param method HTTP method.
     * @param headers HTTP headers.
     * @throws Exception if anything is wrong.
     */
    private void acts(final String method, final String... headers)
            throws Exception {
        new FtRemote(new TkFixed(HELLO_WORLD)).exec(
                new FtRemote.Script() {
                    @Override
                    public void exec(final URI home) throws IOException {
                        MatcherAssert.assertThat(
                                new RsPrint(
                                        new TkProxy(
                                                String.format(FORMAT,
                                                home.getHost(), home.getPort())
                                        ).act(new RqWithHeaders(
                                                new RqFake(method, DASH),
                                                headers))
                                ).print(),
                                Matchers.allOf(
                                        Matchers.containsString(HELLO_WORLD),
                                        Matchers.containsString
                                                (X_TAKES_TK_PROXY_FROM)
                                )
                        );
                    }
                }
        );
    }
}
