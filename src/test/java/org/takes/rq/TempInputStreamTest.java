/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link TempInputStream}.
 * @since 0.31
 */
final class TempInputStreamTest {

    @Test
    void deletesTempFile() throws IOException {
        final File file = File.createTempFile("tempfile", ".tmp");
        final BufferedWriter out = Files.newBufferedWriter(file.toPath());
        try {
            out.write("Temp file deletion test");
        } finally {
            out.close();
        }
        final InputStream body = new TempInputStream(
            Files.newInputStream(file.toPath()), file
        );
        try {
            MatcherAssert.assertThat(
                "File is not created!",
                file.exists(),
                Matchers.is(true)
            );
        } finally {
            body.close();
        }
        MatcherAssert.assertThat(
            "File exists after stream closure",
            file.exists(),
            Matchers.is(false)
        );
    }
}
