/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import org.cactoos.bytes.BytesOf;
import org.cactoos.scalar.LengthOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RsBody.ByteArray}.
 *
 * @since 1.15
 */
final class RsBodyByteArrayTest {

    @Test
    void returnsCorrectInputWithByteArray() throws Exception {
        final byte[] bytes =
            new BytesOf("ByteArray returnsCorrectInput!").asBytes();
        MatcherAssert.assertThat(
            "Body content of Body.ByteArray doesn't provide the correct bytes",
            new BytesOf(new RsBody.ByteArray(bytes)).asBytes(),
            new IsEqual<>(bytes)
        );
    }

    @Test
    void returnsCorrectLengthWithStream() throws Exception {
        final byte[] bytes =
            new BytesOf("Stream returnsCorrectLength!").asBytes();
        MatcherAssert.assertThat(
            "Body content of Body.ByteArray doesn't have the correct length",
            new LengthOf(new RsBody.ByteArray(bytes)).value(),
            new IsEqual<>((long) bytes.length)
        );
    }
}
