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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.tk.TkProxy.RqTransformer;
import org.takes.tk.TkProxy.RsTransformer;

/**
 * Test case for {@link org.takes.tk.TkProxy1}.
 * @author Dragan Bozanovic (bozanovicdr@gmail.com)
 * @version $Id$
 * @since 0.25
 */
public final class TkProxyTest {

    /**
     * TkProxy can proxy requests.
     * @throws IOException If some problem inside
     */
    @Test
    public void proxiesRequest() throws IOException {
        final Take take = Mockito.mock(Take.class);
        final Request transformed = Mockito.mock(Request.class);
        final Response response = Mockito.mock(Response.class);
        Mockito.when(take.act(transformed)).thenReturn(response);
        final TkProxy tkProxy = new TkProxy(
            take,
            new RqTransformer() {
                @Override
                public Request transform(final Request request) {
                    return transformed;
                }
            }
         );
        MatcherAssert.assertThat(
            tkProxy.act(new RqFake()),
            Matchers.equalTo(response)
        );
        Mockito.verify(take).act(transformed);
    }

    /**
     * TkProxy can proxy responses.
     * @throws IOException If some problem inside
     */
    @Test
    public void proxiesResponse() throws IOException {
        final Take take = Mockito.mock(Take.class);
        final Request request = Mockito.mock(Request.class);
        final Response transformed = Mockito.mock(Response.class);
        final TkProxy tkProxy = new TkProxy(
            take,
            new RsTransformer() {
                @Override
                public Response transform(final Response response) {
                    return transformed;
                }
            }
        );
        MatcherAssert.assertThat(
            tkProxy.act(request),
            Matchers.equalTo(transformed)
        );
        Mockito.verify(take).act(request);
    }
}
