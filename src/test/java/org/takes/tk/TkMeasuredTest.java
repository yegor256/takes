/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.rq.RqFake;
import org.takes.rs.RsHeadPrint;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;
import org.takes.rs.RsWithHeader;

/**
 * Test case for {@link TkMeasured}.
 *
 * @since 0.10
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class TkMeasuredTest {

    @Test
    void createsMeasuredResponse() throws Exception {
        final String header = "X-Takes-Millis";
        MatcherAssert.assertThat(
            "TkMeasured didn't add default X-Takes-Millis header to response",
            new RsPrint(
                new TkMeasured(new TkText("default header response")).act(
                    new RqFake()
                )
            ),
            new HasString(header)
        );
    }

    @Test
    void createsMeasuredResponseWithCustomHeader() throws Exception {
        final String header = "X-Custom-Take-Millis";
        MatcherAssert.assertThat(
            "TkMeasured didn't add custom timing header to response",
            new RsPrint(
                new TkMeasured(
                    new TkText("custom header response"),
                    header
                ).act(new RqFake())
            ),
            new HasString(header)
        );
    }

    @Test
    void addsNumericDurationToDefaultHeader() throws Exception {
        MatcherAssert.assertThat(
            "TkMeasured didn't put a numeric duration into X-Takes-Millis header",
            new RsHeadPrint(
                new TkMeasured(new TkText("numeric")).act(new RqFake())
            ).asString(),
            Matchers.matchesRegex("(?s).*\\RX-Takes-Millis: \\d+\\R.*")
        );
    }

    @Test
    void addsNumericDurationToCustomHeader() throws Exception {
        MatcherAssert.assertThat(
            "TkMeasured didn't put a numeric duration into custom header",
            new RsHeadPrint(
                new TkMeasured(
                    new TkText("custom numeric"),
                    "X-Custom-Millis"
                ).act(new RqFake())
            ).asString(),
            Matchers.matchesRegex("(?s).*\\RX-Custom-Millis: \\d+\\R.*")
        );
    }

    @Test
    void measuresTimeSpentInWrappedTake() throws Exception {
        final String header = "X-Slow-Millis";
        MatcherAssert.assertThat(
            "TkMeasured reported less time than the wrapped take spent",
            Long.parseLong(
                new RsHeadPrint(
                    new TkMeasured(
                        req -> {
                            TimeUnit.MILLISECONDS.sleep(50L);
                            return new RsText("slow");
                        },
                        header
                    ).act(new RqFake())
                ).asString().replaceAll(
                    String.format("(?s).*\\R%s: (\\d+)\\R.*", header),
                    "$1"
                )
            ),
            Matchers.greaterThanOrEqualTo(50L)
        );
    }

    @Test
    void keepsHeadersOfWrappedResponse() throws Exception {
        MatcherAssert.assertThat(
            "TkMeasured didn't keep headers of the wrapped response",
            new RsHeadPrint(
                new TkMeasured(
                    req -> new RsWithHeader(
                        new RsText("with header"), "X-Origin", "kept"
                    )
                ).act(new RqFake())
            ),
            new HasString("X-Origin: kept")
        );
    }

    @Test
    void keepsBodyOfWrappedResponse() throws Exception {
        final String body = "measured body";
        MatcherAssert.assertThat(
            "TkMeasured didn't keep body of the wrapped response",
            new RsPrint(
                new TkMeasured(new TkText(body)).act(new RqFake())
            ).printBody(),
            Matchers.equalTo(body)
        );
    }
}
