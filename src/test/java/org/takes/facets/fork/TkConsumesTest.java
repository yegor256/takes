/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.util.Arrays;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.StartsWith;
import org.takes.HttpException;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsJson;
import org.takes.rs.RsPrint;
import org.takes.tk.TkFixed;
import org.takes.tk.TkText;

/**
 * Test case for {@link TkConsumes}.
 * @since 1.0
 */
final class TkConsumesTest {

    /**
     * Application json.
     */
    private static final String APPLICATION_JSON = "application/json";

    @Test
    void acceptsCorrectContentTypeRequest() throws Exception {
        final String contenttype = "Content-Type: application/json";
        final Take consumes = new TkConsumes(
            new TkFixed(new RsJson(new RsEmpty())),
            TkConsumesTest.APPLICATION_JSON
        );
        final Response response = consumes.act(
            new RqFake(
                Arrays.asList(
                    "GET /?TkConsumes",
                    contenttype
                ),
                ""
            )
        );
        MatcherAssert.assertThat(
            "Response must start with correct HTTP status and content type",
            new RsPrint(response),
            new StartsWith(
                new Joined(
                    "\r\n",
                    "HTTP/1.1 200 OK",
                    contenttype
                )
            )
        );
    }

    @Test
    void failsOnUnsupportedAcceptHeader() {
        Assertions.assertThrows(
            HttpException.class,
            () -> {
                final Take consumes = new TkConsumes(
                    new TkFixed(new RsJson(new RsEmpty())),
                    TkConsumesTest.APPLICATION_JSON
                );
                consumes.act(
                    new RqFake(
                        Arrays.asList(
                            "GET /?TkConsumes error",
                            "Content-Type: application/xml"
                        ),
                        ""
                    )
                ).head();
            }
        );
    }

    @Test
    void equalsAndHashCodeEqualTest() {
        final Take take = new TkText("text");
        final String type = "Content-Type: text/plain";
        new Assertion<>(
            "Must evaluate true equality",
            new TkConsumes(take, type),
            new IsEqual<>(new TkConsumes(take, type))
        ).affirm();
    }
}
