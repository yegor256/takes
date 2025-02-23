/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.EndsWith;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.Request;

/**
 * Test case for {@link RqFake}.
 * @since 0.24
 */
@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
final class RqFakeTest {

    @Test
    void conformsToEquality() {
        new Assertion<>(
            "Must evaluate true equality",
            new RqFake(
                "GET",
                "https://localhost:8080"
            ),
            new IsEqual<>(
                new RqFake(
                    "GET",
                    "https://localhost:8080"
                )
            )
        ).affirm();
    }

    @Test
    void printsCorrectly() {
        final RqFake req = new RqFake(
            "GET",
            "/just-a-test HTTP/1.1",
            "test-6=alpha"
        );
        MatcherAssert.assertThat(
            new RqPrint(req),
            Matchers.allOf(
                new HasString("GET /just-a-test HTTP/1.1\r\n"),
                new EndsWith("=alpha")
            )
        );
    }

    @Test
    void printsBodyOnlyOnce() throws IOException {
        final String body = "the body text";
        final Request req = new RqFake("", "", body);
        MatcherAssert.assertThat(
            new RqPrint(req).print(),
            Matchers.containsString(body)
        );
        MatcherAssert.assertThat(
            new RqPrint(req).print(),
            Matchers.not(Matchers.containsString(body))
        );
    }

}
