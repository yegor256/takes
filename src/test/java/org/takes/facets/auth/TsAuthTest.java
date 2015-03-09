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
package org.takes.facets.auth;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.takes.Request;
import org.takes.Takes;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.rq.RqFake;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsPrint;
import org.takes.tk.TkText;
import org.takes.ts.TsFixed;

/**
 * Test case for {@link TsAuth}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.9
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class TsAuthTest {

    /**
     * TsAuth can login a user.
     * @throws IOException If some problem inside
     */
    @Test
    public void logsUserIn() throws IOException {
        final Pass pass = new PsFixed(new Identity.Simple("urn:test:1"));
        final Takes takes = Mockito.mock(Takes.class);
        Mockito.doReturn(new TkText()).when(takes)
            .route(Mockito.any(Request.class));
        new TsAuth(takes, pass).route(new RqFake()).act();
        final ArgumentCaptor<Request> captor =
            ArgumentCaptor.forClass(Request.class);
        Mockito.verify(takes).route(captor.capture());
        MatcherAssert.assertThat(
            new RqHeaders(captor.getValue()).header(
                TsAuth.class.getSimpleName()
            ),
            Matchers.hasItem("urn%3Atest%3A1")
        );
    }

    /**
     * TsAuth can login a user via cookie.
     * @throws IOException If some problem inside
     */
    @Test
    public void logsInUserViaCookie() throws IOException {
        final Pass pass = new PsChain(
            new PsCookie(new CcPlain()),
            new PsLogout()
        );
        MatcherAssert.assertThat(
            new RsPrint(
                new TsAuth(new TsFixed(new TkText()), pass).route(
                    new RqWithHeader(
                        new RqFake(),
                        String.format(
                            "Cookie:  %s=%s",
                            PsCookie.class.getSimpleName(),
                            "urn%3Atest%3A0"
                        )
                    )
                ).act()
            ).print(),
            Matchers.containsString("Set-Cookie: PsCookie=urn%3Atest%3A0")
        );
    }

    /**
     * TsAuth can logout a user.
     * @throws IOException If some problem inside
     */
    @Test
    public void logsUserOut() throws IOException {
        final Pass pass = new PsLogout();
        final Takes takes = Mockito.mock(Takes.class);
        Mockito.doReturn(new TkText()).when(takes)
            .route(Mockito.any(Request.class));
        new TsAuth(takes, pass).route(
            new RqWithHeader(
                new RqFake(),
                TsAuth.class.getSimpleName(),
                "urn%3Atest%3A2"
            )
        ).act();
        final ArgumentCaptor<Request> captor =
            ArgumentCaptor.forClass(Request.class);
        Mockito.verify(takes).route(captor.capture());
        MatcherAssert.assertThat(
            new RqHeaders(captor.getValue()).header(
                TsAuth.class.getSimpleName()
            ),
            Matchers.emptyIterable()
        );
    }

    /**
     * TsAuth can logout a user when a login cookie is present.
     * @throws IOException If some problem inside
     */
    @Test
    public void logsUserOutWithCookiePresent() throws IOException {
        final Pass pass = new PsChain(
            new PsLogout(),
            new PsCookie(new CcPlain())
        );
        MatcherAssert.assertThat(
            new RsPrint(
                new TsAuth(new TsFixed(new TkText()), pass).route(
                    new RqWithHeader(
                        new RqFake(),
                        String.format(
                            "Cookie: %s=%s",
                            PsCookie.class.getSimpleName(),
                            "urn%3Atest%3A5"
                        )
                    )
                ).act()
            ).print(),
            Matchers.containsString("Set-Cookie: PsCookie=")
        );
    }

}
