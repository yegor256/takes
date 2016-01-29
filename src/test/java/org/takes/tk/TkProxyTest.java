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
 * @author sebing
 * @version $Id$
 * @since 0.25
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
  */
public final class TkProxyTest {

    /**
     * TkProxy can just works on POST.
     * @throws Exception If some problem inside.
     */
    @Test
    public void justWorksOnPost()throws Exception {
        this.justWorks(RqMethod.POST);
    }

    /**
     *TkProxy can just works on GET.
     * @throws Exception If some problem inside.
     */
    @Test
    public void justWorksOnGet()throws Exception {
        this.justWorks(RqMethod.GET);
    }

    /**
     * TkProxy can correctly maps path string GET.
     * @throws Exception If some problem inside.
     */
    @Test
    public void correctlyMapsPathStringOnGet() throws Exception {
        this.correctlyMapsPathString(RqMethod.GET);
    }

    /**
     *TkProxy can correctly maps path string on POST.
     * @throws Exception If some problem inside.
     */
    @Test
    public void correctlyMapsPathStringOnPost() throws Exception {
        this.correctlyMapsPathString(RqMethod.POST);
    }

    /**
     * A private method to do test with multiple httpMethods.
     * @param httpmethod HTTP methods (GET, POST, etc),
     * @throws Exception If some problem inside.
     */
    private void justWorks(final String httpmethod) throws Exception {
        new FtRemote(new TkFixed("hello, world!")).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        new RsPrint(
                            new TkProxy(home).act(new RqFake(httpmethod))
                        ).print(),
                        Matchers.containsString("hello")
                    );
                }
            }
        );
    }

    /**
     * A private method to call the TkProxy. act with httpMethod.
     *
     * @param httpmethod HTTP methods (GET, POST, etc),
     * @throws Exception If some problem inside.
     */
    private void correctlyMapsPathString(final String httpmethod)
        throws Exception {
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
                                new RqFake(httpmethod, "/a/b/c")
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
