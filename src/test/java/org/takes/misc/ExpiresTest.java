/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.misc;

import java.time.Instant;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link Expires} interface and direct implementations.
 *
 * @since 2.0
 */
final class ExpiresTest {

    @Test
    void returnsExpired() {
        MatcherAssert.assertThat(
            "Wrong expiration time for expired time",
            new Expires.Expired().print(),
            new IsEqual<>("Expires=0")
        );
    }

    @Test
    void returnsNever() {
        MatcherAssert.assertThat(
            "Wrong expiration time for never",
            new Expires.Never().print(),
            new IsEqual<>("Expires=Thu, 01 Jan 1970 00:00:00 GMT")
        );
    }

    @Test
    void returnsHourAfterGivenTime() {
        MatcherAssert.assertThat(
            "Hour does not shift the given time one hour forward",
            new Expires.Hour(
                Instant.ofEpochSecond(7200).toEpochMilli()
            ).print(),
            new IsEqual<>("Expires=Thu, 01 Jan 1970 03:00:00 GMT")
        );
    }

    @Test
    void returnsExpiresStringInGmt() {
        MatcherAssert.assertThat(
            "Wrong expiration time in GMT",
            new Expires.Date(
                1_517_048_057_117L
            ).print(),
            new IsEqual<>("Expires=Sat, 27 Jan 2018 10:14:17 GMT")
        );
    }
}
