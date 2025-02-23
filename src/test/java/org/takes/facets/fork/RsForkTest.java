/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.Request;
import org.takes.rq.RqFake;
import org.takes.rs.RsBodyPrint;
import org.takes.rs.RsText;

/**
 * Test case for {@link RsFork}.
 * @since 0.6
 */
final class RsForkTest {

    @Test
    void negotiatesContent() throws IOException {
        final Request req = new RqFake(
            Arrays.asList(
                "GET /hello.html",
                "Accept: text/xml; q=0.3, text/plain; q=0.1",
                "Accept: */*; q=0.05"
            ),
            ""
        );
        MatcherAssert.assertThat(
            new RsBodyPrint(
                new RsFork(
                    req,
                    new FkTypes("text/plain", new RsText("it's a text")),
                    new FkTypes("image/*", new RsText("it's an image"))
                )
            ).asString(),
            Matchers.endsWith("a text")
        );
    }

    @Test
    void negotiatesContentWithoutAccept() throws IOException {
        MatcherAssert.assertThat(
            new RsBodyPrint(
                new RsFork(
                    new RqFake(),
                    new FkTypes("image/png,*/*", new RsText("a png"))
                )
            ).asString(),
            Matchers.endsWith("png")
        );
    }

    @Test
    void negotiatesContentWithComplexHeader() throws IOException {
        final Request req = new RqFake(
            Arrays.asList(
                "GET /hell-1o.html",
                "Accept: text/xml"
            ),
            ""
        );
        MatcherAssert.assertThat(
            new RsBodyPrint(
                new RsFork(
                    req,
                    new FkTypes(
                        "application/xml,text/xml",
                        new RsText("how are you?")
                    )
                )
            ).asString(),
            Matchers.endsWith("you?")
        );
    }

    @Test
    void dispatchesByRequestMethod() throws IOException {
        MatcherAssert.assertThat(
            new RsBodyPrint(
                new RsFork(
                    new RqFake("POST", "/test?1", "alpha=1"),
                    new FkMethods("GET", new RsText("it's a GET")),
                    new FkMethods("POST,PUT", new RsText("it's a POST"))
                )
            ).asString(),
            Matchers.endsWith("a POST")
        );
    }

}
