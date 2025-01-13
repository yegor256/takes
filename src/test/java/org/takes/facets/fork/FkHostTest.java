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

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.HttpException;
import org.takes.rq.RqEmpty;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsEmpty;
import org.takes.tk.TkEmpty;
import org.takes.tk.TkText;

/**
 * Test case for {@link FkHost}.
 * @since 0.32
 */
final class FkHostTest {

    @Test
    void matchesByHost() throws Exception {
        MatcherAssert.assertThat(
            new FkHost("www.foo.com", new TkText("boom"))
                .route(
                    new RqWithHeader(
                        new RqEmpty(),
                        "Host: www.foo.com"
                    )
                )
                .has(),
            Matchers.is(true)
        );
    }

    @Test
    void doesntMatchByHost() throws Exception {
        final AtomicBoolean acted = new AtomicBoolean();
        MatcherAssert.assertThat(
            new FkHost(
                "google.com",
                req -> {
                    acted.set(true);
                    return new RsEmpty();
                }
            ).route(new RqFake("PUT", "/?test")).has(),
            Matchers.is(false)
        );
        MatcherAssert.assertThat(acted.get(), Matchers.is(false));
    }

    @Test
    void doesntMatchWithNoHost() {
        Assertions.assertThrows(
            HttpException.class,
            () -> new FkHost("google.com", new TkEmpty())
                .route(new RqFake(Arrays.asList("GET / HTTP/1.1"), "body"))
        );
    }

}
