/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.multipart;

import java.io.File;
import java.nio.file.Files;
import org.cactoos.bytes.BytesOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.Request;

/**
 * Test case for {@link RqTemp}.
 * @since 0.33
 */
final class RqTempTest {

    @Test
    void deletesTempFile() throws Exception {
        final File file = File.createTempFile(
            RqTempTest.class.getName(),
            ".tmp"
        );
        Files.write(
            file.toPath(),
            new BytesOf("Temp file deletion test").asBytes()
        );
        final Request request = new RqTemp(file);
        try {
            MatcherAssert.assertThat(
                "File is not created!",
                file.exists(),
                Matchers.is(true)
            );
        } finally {
            request.body().close();
        }
        MatcherAssert.assertThat(
            "File exists after stream closure",
            file.exists(),
            Matchers.is(false)
        );
    }
}
