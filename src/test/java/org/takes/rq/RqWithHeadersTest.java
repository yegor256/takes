/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import org.cactoos.text.Joined;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.StartsWith;
import org.takes.Request;

/**
 * Test case for {@link RqWithHeaders}.
 * @since 1.0
 */
final class RqWithHeadersTest {

    @Test
    void addsHeadersToRequest() throws IOException {
        final String testheader = "TestHeader: someValue";
        final String someheader = "SomeHeader: testValue";
        MatcherAssert.assertThat(
            new TextOf(
                new RqPrint(
                    new RqWithHeaders(
                        new RqFake(),
                        testheader,
                        someheader
                    )
                ).print()
            ),
            new StartsWith(
                new Joined(
                    "\r\n",
                    "GET / HTTP/1.1",
                    "Host: www.example.com",
                    testheader,
                    someheader
                )
            )
        );
    }

    @Test
    void mustEqualTest() {
        final Request request = new RqWithHeader(
            new RqFake(),
            "jsessionid", "abcdefghigklmnop"
        );
        new Assertion<>(
            "Must evaluate true equality",
            new RqWithHeaders(
                request,
                "clusterNode: 5"
            ),
            new IsEqual<>(
                new RqWithHeaders(
                    request,
                    "clusterNode: 5"
                )
            )
        ).affirm();
    }
}
