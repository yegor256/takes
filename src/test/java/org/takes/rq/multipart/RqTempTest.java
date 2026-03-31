/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
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
    void deletesTempFileOnClose() throws Exception {
        final File file = File.createTempFile(
            RqTempTest.class.getName(),
            ".tmp"
        );
        Files.write(
            file.toPath(),
            new BytesOf("Temp file deletion test").asBytes()
        );
        new RqTemp(file).body().close();
        MatcherAssert.assertThat(
            "File exists after stream closure",
            file.exists(),
            Matchers.is(false)
        );
    }
}
