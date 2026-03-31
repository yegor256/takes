/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
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
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class TempInputStreamTest {

    @Test
    void isAvailableForReading() throws IOException {
        final File file = File.createTempFile("tempfile-avail", ".tmp");
        try (BufferedWriter out = Files.newBufferedWriter(file.toPath())) {
            out.write("Temp file reading test");
        }
        try (InputStream body = new TempInputStream(
            Files.newInputStream(file.toPath()), file
        )) {
            MatcherAssert.assertThat(
                "TempInputStream must be available for reading",
                body.available(),
                Matchers.greaterThanOrEqualTo(0)
            );
        }
    }

    @Test
    void deletesTempFileOnClose() throws IOException {
        final File file = File.createTempFile("tempfile-delete", ".tmp");
        try (BufferedWriter out = Files.newBufferedWriter(file.toPath())) {
            out.write("Temp file deletion test");
        }
        new TempInputStream(Files.newInputStream(file.toPath()), file).close();
        MatcherAssert.assertThat(
            "File exists after stream closure",
            file.exists(),
            Matchers.is(false)
        );
    }
}
