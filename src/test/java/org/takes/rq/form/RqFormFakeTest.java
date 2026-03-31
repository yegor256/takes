/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.form;

import java.net.URLEncoder;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rq.RqForm;

/**
 * Test case for {@link RqFormFake}.
 * @since 0.33
 */
final class RqFormFakeTest {

    @Test
    void createsFormRequestWithKeyParam() throws Exception {
        MatcherAssert.assertThat(
            "Request parameter should contain expected values",
            RqFormFakeTest.fakeForm().param("key"),
            Matchers.hasItems("value", "a&b")
        );
    }

    @Test
    void createsFormRequestWithAnotherKeyParam() throws Exception {
        MatcherAssert.assertThat(
            "Request parameter should contain expected additional value",
            RqFormFakeTest.fakeForm().param("anotherkey"),
            Matchers.hasItems("againanothervalue")
        );
    }

    @Test
    void createsFormRequestWithAllParamNames() throws Exception {
        MatcherAssert.assertThat(
            "Request should contain all parameter names",
            RqFormFakeTest.fakeForm().names(),
            Matchers.hasItems("key", "anotherkey")
        );
    }

    @Test
    void throwsExceptionWhenNotCorrectlyCreated() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new RqFormFake(
                new RqFake(),
                "param"
            )
        );
    }

    private static RqForm fakeForm() throws Exception {
        return new RqFormFake(
            new RqFake(
                new ListOf<>(
                    "GET /form",
                    "Host: www.example5.com",
                    String.format(
                        "Content-Length: %d",
                        URLEncoder.encode(
                            "key=value&key=a&b&anotherkey=againanothervalue",
                            "UTF-8"
                        ).length()
                    )
                ),
                ""
            ),
            "key", "value",
            "key", "a&b",
            "anotherkey", "againanothervalue"
        );
    }
}
