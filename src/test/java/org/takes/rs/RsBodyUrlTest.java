/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.OutputStream;
import org.cactoos.Bytes;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.OutputTo;
import org.cactoos.io.TeeInput;
import org.cactoos.io.TempFile;
import org.cactoos.scalar.LengthOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RsBody.Url}.
 *
 * @since 1.15
 */
final class RsBodyUrlTest {

    @Test
    void returnsCorrectInputWithUrl() throws Exception {
        try (TempFile file = new TempFile()) {
            final Bytes body = new BytesOf("URL returnsCorrectInput!");
            try (OutputStream out = new OutputTo(file.value()).stream()) {
                new LengthOf(
                    new TeeInput(body, new OutputTo(out))
                ).value();
            }
            final RsBody.Url input = new RsBody.Url(file.value().toUri().toURL());
            MatcherAssert.assertThat(
                "Body content of Body.Url doesn't provide the correct bytes",
                new BytesOf(input).asBytes(),
                new IsEqual<>(body.asBytes())
            );
        }
    }

    @Test
    void returnsCorrectLengthWithUrl() throws Exception {
        try (TempFile file = new TempFile()) {
            final Bytes body = new BytesOf("URL returnsCorrectLength!");
            try (OutputStream out = new OutputTo(file.value()).stream()) {
                new LengthOf(
                    new TeeInput(body, new OutputTo(out))
                ).value();
            }
            MatcherAssert.assertThat(
                "Body content of Body.Url doesn't have the correct length",
                new LengthOf(
                    new RsBody.Url(file.value().toUri().toURL())
                ).value(),
                new IsEqual<>((long) body.asBytes().length)
            );
        }
    }
}
