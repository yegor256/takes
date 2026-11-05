/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@code README.md}.
 *
 * @since 2.0
 */
final class ReadmeTest {

    @Test
    void doesNotInstantiateRqFormInterface() throws IOException {
        MatcherAssert.assertThat(
            "README must not instantiate the RqForm interface",
            new String(
                Files.readAllBytes(Paths.get("README.md")),
                StandardCharsets.UTF_8
            ),
            Matchers.not(Matchers.containsString("new RqForm("))
        );
    }
}
