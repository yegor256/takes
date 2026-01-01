/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.servlet;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link  ServletConnectionFake}.
 *
 * @since 2.0
 */
final class ServletConnectionFakeTest {
    @Test
    void connectionIdEmptyCtor() {
        final ServletConnectionFake conn = new ServletConnectionFake();
        MatcherAssert.assertThat(
            "Should be localhost",
            conn.getConnectionId(),
            Matchers.equalTo("localhost")
        );
    }

    @Test
    void protocolEmptyCtor() {
        final ServletConnectionFake conn = new ServletConnectionFake();
        MatcherAssert.assertThat(
            "Should be HTTP/1.0",
            conn.getProtocol(),
            Matchers.equalTo("HTTP/1.0")
        );
    }

    @Test
    void secureEmptyCtor() {
        final ServletConnectionFake conn = new ServletConnectionFake();
        MatcherAssert.assertThat(
            "Should be false",
            conn.isSecure(),
            Matchers.equalTo(false)
        );
    }

    @Test
    void connectionId() {
        final ServletConnectionFake conn = new ServletConnectionFake("123", null, false);
        MatcherAssert.assertThat(
            "Should be 123",
            conn.getConnectionId(),
            Matchers.equalTo("123")
        );
    }

    @Test
    void protocol() {
        final ServletConnectionFake conn = new ServletConnectionFake(null, "HTTP", false);
        MatcherAssert.assertThat(
            "Should be HTTP",
            conn.getProtocol(),
            Matchers.equalTo("HTTP")
        );
    }

    @Test
    void secure() {
        final ServletConnectionFake conn = new ServletConnectionFake(null, null, true);
        MatcherAssert.assertThat(
            "Should be true",
            conn.isSecure(),
            Matchers.equalTo(true)
        );
    }
}
