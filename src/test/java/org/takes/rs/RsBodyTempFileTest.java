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
 * Test case for {@link RsBody.TempFile}.
 *
 * @since 1.15
 */
final class RsBodyTempFileTest {

    @Test
    void returnsCorrectInputWithStream() throws Exception {
        final byte[] bytes =
            new BytesOf("Stream returnsCorrectInput!").asBytes();
        MatcherAssert.assertThat(
            "Body content of Body.TempFile doesn't provide the correct bytes",
            new BytesOf(
                new RsBody.TempFile(new RsBody.ByteArray(bytes))
            ).asBytes(),
            new IsEqual<>(bytes)
        );
    }

    @Test
    void returnsCorrectLengthWithTempFile() throws Exception {
        final byte[] bytes =
            new BytesOf("TempFile returnsCorrectLength!").asBytes();
        MatcherAssert.assertThat(
            "Body content of Body.TempFile doesn't have the correct length",
            new LengthOf(
                new RsBody.TempFile(new RsBody.ByteArray(bytes))
            ).value(),
            new IsEqual<>((long) bytes.length)
        );
    }
}
