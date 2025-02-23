/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.StartsWith;
import org.takes.HttpException;
import org.takes.rq.RqFake;
import org.takes.rq.RqMethod;
import org.takes.rs.RsHeadPrint;

/**
 * Test case for {@link TkClasspath}.
 * @since 0.1
 */
final class TkClasspathTest {

    @Test
    void dispatchesByResourceName() throws Exception {
        MatcherAssert.assertThat(
            "Response should start with HTTP/1.1 200 OK",
            new RsHeadPrint(
                new TkClasspath().act(
                    new RqFake(
                        RqMethod.GET, "/org/takes/Take.class?a", ""
                    )
                )
            ),
            new StartsWith("HTTP/1.1 200 OK")
        );
    }

    @Test
    void throwsWhenResourceNotFound() throws Exception {
        try {
            new TkClasspath().act(
                new RqFake(RqMethod.PUT, "/something-else", "")
            );
            MatcherAssert.assertThat(
                "Expected HttpException to be thrown",
                false
            );
        } catch (final HttpException exception) {
            MatcherAssert.assertThat(
                "Exception should have HTTP_NOT_FOUND status code",
                exception.code(),
                Matchers.equalTo(HttpURLConnection.HTTP_NOT_FOUND)
            );
        }
    }
}
