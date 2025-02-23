/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import org.cactoos.bytes.BytesOf;
import org.cactoos.io.InputOf;
import org.cactoos.scalar.LengthOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RsBody.Stream}.
 *
 * @since 1.15
 */
final class RsBodyStreamTest {

    @Test
    void returnsCorrectInputWithStream() throws Exception {
        final byte[] bytes =
            new BytesOf("Stream returnsCorrectInput!").asBytes();
        MatcherAssert.assertThat(
            "Body content of Body.Stream doesn't provide the correct bytes",
            new BytesOf(
                new RsBody.Stream(new InputOf(bytes).stream())
            ).asBytes(),
            new IsEqual<>(bytes)
        );
    }

    @Test
    void returnsCorrectLengthWithStream() throws Exception {
        final byte[] bytes =
            new BytesOf("Stream returnsCorrectLength!").asBytes();
        MatcherAssert.assertThat(
            "Body content of Body.Stream doesn't have the correct length",
            new LengthOf(
                new RsBody.Stream(new InputOf(bytes).stream())
            ).value(),
            new IsEqual<>((long) bytes.length)
        );
    }
}
