/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.StartsWith;
import org.takes.HttpException;
import org.takes.rq.RqFake;
import org.takes.rs.RsHeadPrint;

/**
 * Test case for {@link TkClasspath}.
 * @since 0.1
 */
final class TkClasspathTest {

    @Test
    void dispatchesByResourceName() throws Exception {
        MatcherAssert.assertThat(
            "TkClasspath must serve existing classpath resources with HTTP OK status",
            new RsHeadPrint(
                new TkClasspath().act(
                    new RqFake(
                        "GET", "/org/takes/Take.class?a", ""
                    )
                )
            ),
            new StartsWith("HTTP/1.1 200 OK")
        );
    }

    @Test
    void throwsWhenResourceNotFound() {
        Assertions.assertThrows(
            HttpException.class,
            () -> new TkClasspath().act(
                new RqFake("PUT", "/something-else", "")
            )
        );
    }
}
