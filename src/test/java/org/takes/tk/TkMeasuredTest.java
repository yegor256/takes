/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.Response;
import org.takes.facets.hamcrest.HmHeader;
import org.takes.rq.RqFake;
import org.takes.rs.RsBodyPrint;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;

/**
 * Test case for {@link TkMeasured}.
 *
 * @since 0.10
 */
final class TkMeasuredTest {

    @Test
    void createsMeasuredResponse() throws Exception {
        MatcherAssert.assertThat(
            "TkMeasured must add numeric duration to default header",
            new TkMeasured(new TkText("default header response")).act(
                new RqFake()
            ),
            new HmHeader<>(
                "X-Takes-Millis",
                Matchers.hasItems(Matchers.matchesPattern("\\d+"))
            )
        );
    }

    @Test
    void createsMeasuredResponseWithCustomHeader() throws Exception {
        MatcherAssert.assertThat(
            "TkMeasured must add numeric duration to custom header",
            new TkMeasured(
                new TkText("custom header response"),
                "X-Custom-Take-Millis"
            ).act(new RqFake()),
            new HmHeader<>(
                "X-Custom-Take-Millis",
                Matchers.hasItems(Matchers.matchesPattern("\\d+"))
            )
        );
    }

    @Test
    void measuresElapsedTimeOfWrappedTake() throws Exception {
        MatcherAssert.assertThat(
            "TkMeasured must report the real elapsed processing time",
            new TkMeasured(
                req -> {
                    Response rsp;
                    try {
                        TimeUnit.MILLISECONDS.sleep(100L);
                        rsp = new TkText("slow response").act(req);
                    } catch (final InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        rsp = new RsText("interrupted response");
                    }
                    return rsp;
                }
            ).act(new RqFake()),
            new HmHeader<>(
                "X-Takes-Millis",
                Matchers.hasItems(Matchers.matchesPattern("[1-9][0-9]{2,}"))
            )
        );
    }

    @Test
    void keepsExistingTimingHeader() throws Exception {
        MatcherAssert.assertThat(
            "TkMeasured must keep an existing timing header",
            new TkMeasured(
                new TkWithHeader(
                    new TkText("existing header response"),
                    "X-Takes-Millis",
                    "99999"
                )
            ).act(new RqFake()),
            new HmHeader<>("X-Takes-Millis", Matchers.hasItems("99999"))
        );
    }

    @Test
    void keepsResponseBodyUnchanged() throws Exception {
        final String body = "measured body response";
        MatcherAssert.assertThat(
            "TkMeasured must not modify the response body",
            new RsBodyPrint(
                new TkMeasured(new TkText(body)).act(new RqFake())
            ).asString(),
            Matchers.equalTo(body)
        );
    }

    @Test
    void failsOnEmptyCustomHeaderName() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new RsPrint(
                new TkMeasured(new TkText("empty header response"), "").act(
                    new RqFake()
                )
            ).printHead(),
            "TkMeasured must reject empty header name by RFC 7230"
        );
    }

    @Test
    void addsTimingHeaderWhenNameIsNull() throws Exception {
        MatcherAssert.assertThat(
            "TkMeasured must add the timing header even for null name",
            new TkMeasured(new TkText("null header response"), null).act(
                new RqFake()
            ),
            new HmHeader<>(
                "null",
                Matchers.hasItems(Matchers.matchesPattern("\\d+"))
            )
        );
    }
}
