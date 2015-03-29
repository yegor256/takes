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
import org.takes.Request;
import org.takes.Take;
import org.takes.Takes;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.facets.forward.RsForward;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsEmpty;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link TsSecure}.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.11
 */
public final class TsSecureTest {

    /**
     * TsSecure can fail on anonymous access.
     * @throws IOException If some problem inside
     */
    @Test(expected = RsForward.class)
    public void failsOnAnonymous() throws IOException {
        new TsSecure(
            new Takes() {
                @Override
                public Take route(final Request request) throws IOException {
                    return new TkEmpty();
                }
            }
        ).route(new RqFake()).act();
    }

    /**
     * TsSecure can pass on registered user.
     * @throws IOException If some problem inside
     */
    @Test
    public void passesOnRegisteredUser() throws IOException {
        MatcherAssert.assertThat(
            new TsSecure(
                new Takes() {
                    @Override
                    public Take route(final Request request)
                        throws IOException {
                        return new TkEmpty();
                    }
                }
            ).route(
                new RqWithHeader(
                    new RqFake(),
                    TsAuth.class.getSimpleName(),
                    new String(
                        new CcPlain().encode(new Identity.Simple("urn:test:1"))
                    )
                )
            ).act(),
            Matchers.instanceOf(RsEmpty.class)
        );
    }
}
