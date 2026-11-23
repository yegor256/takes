/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.security.SecureRandom;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link OptionsTest}.
 *
 * @since 0.9
 */
final class OptionsTest {

    @Test
    void understandsHitRefreshArg() {
        MatcherAssert.assertThat(
            "Hit refresh option should be enabled",
            new Options("--hit-refresh --threads=2".split(" ")).hitRefresh(),
            Matchers.is(true)
        );
    }

    @Test
    void understandsThreadsArg() {
        MatcherAssert.assertThat(
            "Thread count should be set to 2",
            new Options("--hit-refresh --threads=2".split(" ")).threads(),
            Matchers.is(2)
        );
    }

    @Test
    void understandsIdleArg() {
        final int idle = 1 + new SecureRandom().nextInt(Integer.MAX_VALUE);
        MatcherAssert.assertThat(
            "Idle timeout was not taken from the --idle argument",
            new Options(
                "--threads=3", String.format("--idle=%d", idle)
            ).idle(),
            Matchers.is(idle)
        );
    }

    @Test
    void waitsFiveSecondsForIdleConnectionByDefault() {
        MatcherAssert.assertThat(
            "Idle timeout did not default to five seconds",
            new Options("--hit-refresh", "--threads=7").idle(),
            Matchers.is(5000)
        );
    }

    @Test
    void rejectsZeroIdle() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new Options("--threads=2", "--idle=0").idle()
        );
    }

    @Test
    void rejectsNegativeIdle() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new Options(
                String.format(
                    "--idle=-%d",
                    1 + new SecureRandom().nextInt(Integer.MAX_VALUE)
                )
            ).idle()
        );
    }

    @Test
    void rejectsIdleWithoutValueWithRangeMessage() {
        MatcherAssert.assertThat(
            "Missing --idle value was not rejected with the range message",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Options("--idle", "--threads=4").idle()
            ).getMessage(),
            Matchers.startsWith("--idle must be between")
        );
    }

    @Test
    void rejectsIdleWithUnitWithRangeMessage() {
        MatcherAssert.assertThat(
            "--idle value with a unit was not rejected with the range message",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Options("--hit-refresh", "--idle=5s").idle()
            ).getMessage(),
            Matchers.startsWith("--idle must be between")
        );
    }

    @Test
    void rejectsIdleBeyondIntegerRange() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new Options("--idle=3000000000", "--daemon").idle()
        );
    }
}
