/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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

import org.cactoos.iterable.IterableOf;
import org.cactoos.text.FormattedText;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.AllOf;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasString;
import org.llorllale.cactoos.matchers.StartsWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.takes.Request;
import org.takes.Take;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.rq.RqFake;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;
import org.takes.tk.TkText;

/**
 * Test case for {@link TkAuth}.
 * @since 0.9
 */
final class TkAuthTest {

    @Test
    void logsUserIn() throws Exception {
        final Pass pass = new PsFixed(new Identity.Simple("urn:test:1"));
        final Take take = Mockito.mock(Take.class);
        Mockito.doReturn(new RsText()).when(take)
            .act(Mockito.any(Request.class));
        new TkAuth(take, pass).act(new RqFake());
        final ArgumentCaptor<Request> captor =
            ArgumentCaptor.forClass(Request.class);
        Mockito.verify(take).act(captor.capture());
        MatcherAssert.assertThat(
            new RqHeaders.Base(captor.getValue()).header(
                TkAuth.class.getSimpleName()
            ),
            Matchers.hasItem("urn%3Atest%3A1")
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void logsInUserViaCookie() throws Exception {
        new Assertion<>(
            "Response with header Set-Cookie",
            new RsPrint(
                new TkAuth(
                    new TkText(),
                    new PsChain(
                        new PsCookie(new CcPlain()),
                        new PsLogout()
                    )
                ).act(
                    new RqWithHeader(
                        new RqFake(),
                        new FormattedText(
                            "Cookie:  %s=%s",
                            PsCookie.class.getSimpleName(),
                            "urn%3Atest%3A0"
                        ).asString()
                    )
                )
            ),
            new AllOf<>(
                new IterableOf<>(
                    new StartsWith("HTTP/1.1 200 "),
                    new HasString("Set-Cookie: PsCookie=urn%3Atest%3A0")
                )
            )
        ).affirm();
    }

    @Test
    void logsUserOut() throws Exception {
        final Pass pass = new PsLogout();
        final Take take = Mockito.mock(Take.class);
        Mockito.doReturn(new RsText()).when(take)
            .act(Mockito.any(Request.class));
        new TkAuth(take, pass).act(
            new RqWithHeader(
                new RqFake(),
                TkAuth.class.getSimpleName(),
                "urn%3Atest%3A2"
            )
        );
        final ArgumentCaptor<Request> captor =
            ArgumentCaptor.forClass(Request.class);
        Mockito.verify(take).act(captor.capture());
        MatcherAssert.assertThat(
            new RqHeaders.Base(captor.getValue()).header(
                TkAuth.class.getSimpleName()
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void logsUserOutWithCookiePresent() throws Exception {
        new Assertion<>(
            "Response with header setting empty cookie",
            new RsPrint(
                new TkAuth(
                    new TkText(),
                    new PsChain(
                        new PsLogout(),
                        new PsCookie(new CcPlain())
                    )
                ).act(
                    new RqWithHeader(
                        new RqFake(),
                        String.format(
                            "Cookie: %s=%s",
                            PsCookie.class.getSimpleName(),
                            "urn%3Atest%3A5"
                        )
                    )
                )
            ),
            new HasString("Set-Cookie: PsCookie=")
        ).affirm();
    }

}
