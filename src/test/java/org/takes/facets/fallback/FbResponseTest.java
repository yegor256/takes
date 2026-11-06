/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.ResponseOf;
import org.takes.rs.RsBodyPrint;
import org.takes.rs.RsText;

/**
 * Test case for {@link FbResponse}.
 *
 * @since 2.0
 */
final class FbResponseTest {

    @Test
    void recoversBodyThroughFallback() throws Exception {
        MatcherAssert.assertThat(
            "Body must come from the fallback when the original one fails",
            new RsBodyPrint(
                new FbResponse(
                    new ResponseOf(
                        () -> {
                            throw new IllegalStateException("head");
                        },
                        () -> {
                            throw new IllegalStateException("body");
                        }
                    ),
                    new FbFixed(new RsText("rescued")),
                    new RqFake()
                )
            ).asString(),
            Matchers.equalTo("rescued")
        );
    }
}
