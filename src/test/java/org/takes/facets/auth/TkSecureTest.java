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
package org.takes.facets.auth;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.facets.forward.RsForward;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsEmpty;

/**
 * Test case for {@link TkSecure}.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.11
 */
public final class TkSecureTest {

    /**
     * TkSecure can fail on anonymous access.
     * @throws IOException If some problem inside
     */
    @Test(expected = RsForward.class)
    public void failsOnAnonymous() throws IOException {
        new TkSecure(
            new Take() {
                @Override
                public Response act(final Request request) {
                    return new RsEmpty();
                }
            }
        ).act(new RqFake());
    }

    /**
     * TkSecure can pass on registered user.
     * @throws IOException If some problem inside
     */
    @Test
    public void passesOnRegisteredUser() throws IOException {
        MatcherAssert.assertThat(
            new TkSecure(
                new Take() {
                    @Override
                    public Response act(final Request request) {
                        return new RsEmpty();
                    }
                }
            ).act(
                new RqWithHeader(
                    new RqFake(),
                    TkAuth.class.getSimpleName(),
                    new String(
                        new CcPlain().encode(new Identity.Simple("urn:test:2"))
                    )
                )
            ),
            Matchers.instanceOf(RsEmpty.class)
        );
    }
}
