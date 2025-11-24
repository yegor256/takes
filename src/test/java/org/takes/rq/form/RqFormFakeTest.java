/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
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

    /**
     * Content-Length header template.
     */
    private static final String HEADER = "Content-Length: %d";

    @Test
    void createsFormRequestWithParams() throws Exception {
        final String key = "key";
        final String akey = "anotherkey";
        final String value = "value";
        final String avalue = "a&b";
        final String aavalue = "againanothervalue";
        final RqForm req = new RqFormFake(
            new RqFake(
                new ListOf<>(
                    "GET /form",
                    "Host: www.example5.com",
                    String.format(
                        RqFormFakeTest.HEADER,
                        URLEncoder.encode(
                            "key=value&key=a&b&anotherkey=againanothervalue",
                            "UTF-8"
                        ).length()
                    )
                ),
                ""
            ),
            key, value,
            key, avalue,
            akey, aavalue
        );
        MatcherAssert.assertThat(
            "Request parameter should contain expected values",
            req.param(key),
            Matchers.hasItems(value, avalue)
        );
        MatcherAssert.assertThat(
            "Request parameter should contain expected additional value",
            req.param(akey),
            Matchers.hasItems(aavalue)
        );
        MatcherAssert.assertThat(
            "Request should contain all parameter names",
            req.names(),
            Matchers.hasItems(key, akey)
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
}
