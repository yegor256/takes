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
package org.takes.facets.fallback;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.misc.Opt;
import org.takes.rq.RqFake;
import org.takes.rs.ResponseOf;
import org.takes.rs.RsBodyPrint;
import org.takes.rs.RsText;
import org.takes.tk.TkFailure;

/**
 * Test case for {@link TkFallback}.
 * @since 0.9.6
 */
final class TkFallbackTest {

    @Test
    void fallsBack() throws Exception {
        final String err = "message";
        MatcherAssert.assertThat(
            new RsBodyPrint(
                new TkFallback(
                    new TkFailure(err),
                    req -> new Opt.Single<>(
                        new RsText(req.throwable().getMessage())
                    )
                ).act(new RqFake())
            ).asString(),
            Matchers.endsWith(err)
        );
    }

    @Test
    void fallsBackInsideResponse() throws Exception {
        MatcherAssert.assertThat(
            new RsBodyPrint(
                new TkFallback(
                    req -> new ResponseOf(
                        () -> {
                            throw new UnsupportedOperationException("");
                        },
                        () -> {
                            throw new IllegalArgumentException(
                                "here we fail"
                            );
                        }
                    ),
                    new FbFixed(new RsText("caught here!"))
                ).act(new RqFake())
            ).asString(),
            Matchers.startsWith("caught")
        );
    }

    @Test
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    void fallsBackWithProperMessage() {
        try {
            new TkFallback(
                new TkFailure(),
                new FbChain()
            ).act(new RqFake());
            MatcherAssert.assertThat("Must throw exception", false);
            //@checkstyle IllegalCatch (1 line)
        } catch (final Exception exception) {
            MatcherAssert.assertThat(
                exception.getMessage(),
                Matchers.containsString("fallback ")
            );
        }
    }
}
