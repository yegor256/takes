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
package org.takes.facets.fork;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsEmpty;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link FkTypes}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.9
 */
public final class FkTypesTest {

    /**
     * FkTypes can match by Accept header.
     * @throws IOException If some problem inside
     */
    @Test
    public void matchesByAcceptHeader() throws IOException {
        final String accept = "Accept";
        MatcherAssert.assertThat(
            new FkTypes("text/xml", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), accept, "*/* ")
            ).has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new FkTypes("application/json", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), accept, "image/*")
            ).has(),
            Matchers.is(false)
        );
        MatcherAssert.assertThat(
            new FkTypes("*/*", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), accept, "text/html")
            ).has(),
            Matchers.is(true)
        );
    }

    /**
     * FkTypes can match by Accept header.
     * @throws IOException If some problem inside
     */
    @Test
    public void matchesByCompositeType() throws IOException {
        MatcherAssert.assertThat(
            new FkTypes("text/xml,text/json", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), "Accept ", "text/json")
            ).has(),
            Matchers.is(true)
        );
    }

    /**
     * FkTypes can ignore if no Accept header present.
     * @throws IOException If some problem inside
     */
    @Test
    public void ignoresWithoutHeader() throws IOException {
        MatcherAssert.assertThat(
            new FkTypes("text/plain", new RsEmpty()).route(
                new RqFake()
            ).has(),
            Matchers.is(false)
        );
    }

    /**
     * FkTypes can match if no Accept header present.
     * @throws IOException If some problem inside
     */
    @Test
    public void matchesWithoutHeader() throws IOException {
        MatcherAssert.assertThat(
            new FkTypes("text/plain,*/*", new RsEmpty()).route(
                new RqFake()
            ).has(),
            Matchers.is(true)
        );
    }

    /**
     * FkTypes can rely on a Take to provide the response.
     * @throws IOException If some problem inside
     */
    @Test
    public void reliesOnTake() throws IOException {
        MatcherAssert.assertThat(
            new FkTypes("*/*,text/plain", new TkEmpty()).route(
                new RqFake()
            ).has(),
            Matchers.is(true)
        );
    }
}
