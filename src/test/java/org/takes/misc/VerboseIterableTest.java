/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.misc;

import java.util.Arrays;
import java.util.List;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link VerboseIterable}.
 * @since 0.15.1
 */
final class VerboseIterableTest {

    @Test
    void returnsCorrectSize() {
        final List<String> valid = Arrays.asList(
            "Accept: text/plain",
            "Accept-Charset: utf-8",
            "Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==",
            "Cache-Control: no-cache",
            "From: user@example.com"
        );
        MatcherAssert.assertThat(
            "VerboseIterable must have the same size as the wrapped iterable",
            new VerboseIterable<>(
                valid,
                new TextOf("Empty Error Message")
            ),
            Matchers.iterableWithSize(valid.size())
        );
    }
}
