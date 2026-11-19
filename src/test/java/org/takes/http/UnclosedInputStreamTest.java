/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.misc.StateAwareInputStream;

/**
 * Test case for {@link UnclosedInputStream}.
 *
 * @since 1.20
 */
final class UnclosedInputStreamTest {

    @Test
    void keepsOriginOpenWhenClosed() throws Exception {
        final StateAwareInputStream origin = new StateAwareInputStream(
            new ByteArrayInputStream(
                "ещё не закрыт".getBytes(StandardCharsets.UTF_8)
            )
        );
        new UnclosedInputStream(origin).close();
        MatcherAssert.assertThat(
            "Origin stream was closed through the unclosed wrapper",
            origin.isClosed(),
            Matchers.is(false)
        );
    }
}
