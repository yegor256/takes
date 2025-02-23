/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
