/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link org.takes.rq.RqMethod}.
 * @since 0.9.1
 */
final class RqMethodTest {

    @Test
    void returnsMethod() throws IOException {
        MatcherAssert.assertThat(
            "Request method must be extracted correctly",
            new RqMethod.Base(new RqFake(RqMethod.POST)).method(),
            Matchers.equalTo(RqMethod.POST)
        );
    }

    @Test
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    void supportsAllStandardMethods() throws IOException {
        for (final String method
            : Arrays.asList(
            RqMethod.DELETE, RqMethod.GET, RqMethod.HEAD, RqMethod.OPTIONS,
            RqMethod.PATCH, RqMethod.POST, RqMethod.PUT, RqMethod.TRACE,
            RqMethod.CONNECT
        )
        ) {
            MatcherAssert.assertThat(
                String.format("Request method %s must be supported", method),
                new RqMethod.Base(new RqFake(method)).method(),
                Matchers.equalTo(method)
            );
        }
    }

    @Test
    void supportsExtensionMethods() throws IOException {
        final String method = "CUSTOM";
        MatcherAssert.assertThat(
            "Custom extension method must be supported",
            new RqMethod.Base(new RqFake(method)).method(),
            Matchers.equalTo(method)
        );
    }

    @Test
    void failsOnMissingUri() {
        final RqMethod.Base req = new RqMethod.Base(
            new RqSimple(Arrays.asList("GET"), null)
        );
        Assertions.assertThrows(
            IOException.class,
            req::method
        );
    }

    @Test
    void failsOnExtraLineElement() {
        Assertions.assertThrows(
            IOException.class,
            () -> new RqMethod.Base(
                new RqSimple(Arrays.asList("GET / HTTP/1.1 abc"), null)
            ).method()
        );
    }

    @Test
    void failsOnExtraSpaces() {
        Assertions.assertThrows(
            IOException.class,
            () -> new RqMethod.Base(
                new RqSimple(Arrays.asList("GET /     HTTP/1.1"), null)
            ).method()
        );
    }

    @Test
    void failsOnSeparatorsInExtensionMethod() {
        Assertions.assertThrows(
            IOException.class,
            () -> new RqMethod.Base(new RqFake("CUSTO{M)")).method()
        );
    }
}
