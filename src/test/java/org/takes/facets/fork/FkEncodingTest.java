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
package org.takes.facets.fork;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsEmpty;

/**
 * Test case for {@link FkEncoding}.
 * @since 0.10
 */
final class FkEncodingTest {

    @Test
    void matchesByAcceptEncodingHeader() throws IOException {
        final String header = "Accept-Encoding";
        MatcherAssert.assertThat(
            new FkEncoding("gzip", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), header, "gzip,deflate")
            ).has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new FkEncoding("", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), header, "xz,gzip,exi")
            ).has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new FkEncoding("deflate", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), header, "gzip,exi")
            ).has(),
            Matchers.is(false)
        );
    }

}
