/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.EndsWith;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.Request;

/**
 * Test case for {@link RqFake}.
 * @since 0.24
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class RqFakeTest {

    @Test
    void conformsToEquality() {
        MatcherAssert.assertThat(
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
        );
    }

    @Test
    void printsCorrectly() {
        final RqFake req = new RqFake(
            "GET",
            "/just-a-test HTTP/1.1",
            "test-6=alpha"
        );
        MatcherAssert.assertThat(
            "Request print must contain the correct HTTP method and body ending",
            new RqPrint(req),
            Matchers.allOf(
                new HasString("GET /just-a-test HTTP/1.1\r\n"),
                new EndsWith("=alpha")
            )
        );
    }

    @Test
    void printsBodyOnFirstRead() throws IOException {
        final String body = "the body text";
        MatcherAssert.assertThat(
            "First print must contain the request body",
            new RqPrint(new RqFake("", "", body)).print(),
            Matchers.containsString(body)
        );
    }

    @Test
    void doesNotPrintBodyAfterConsumed() throws IOException {
        final String body = "the body text for consume";
        final Request req = new RqFake("", "", body);
        new RqPrint(req).print();
        MatcherAssert.assertThat(
            "Second print must not contain the body after it was already consumed",
            new RqPrint(req).print(),
            Matchers.not(Matchers.containsString(body))
        );
    }

}
