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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.fork.am.AmVersion;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link FkAgent}.
 * @since 1.7.2
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class FkAgentTest {

    @Test
    void matchesByVersionGreater() throws Exception {
        final String header = "User-Agent";
        final String agent = "Chrome";
        MatcherAssert.assertThat(
            new FkAgent(
                new TkEmpty(),
                new AmVersion(agent, new AmVersion.VmGreater(12))
            ).route(
                new RqWithHeader(
                    new RqFake(),
                    header,
                    "Chrome/63.0.3239.132"
                )
            ).has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new FkAgent(
                new TkEmpty(),
                new AmVersion(agent, new AmVersion.VmGreater(90))
            ).route(
                new RqWithHeader(
                    new RqFake(),
                    header,
                    "Chrome/41.0.2227.0"
                )
            ).has(),
            Matchers.is(false)
        );
    }

}
