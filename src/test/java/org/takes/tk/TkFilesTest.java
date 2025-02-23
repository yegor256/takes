/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.llorllale.cactoos.matchers.StartsWith;
import org.takes.HttpException;
import org.takes.rq.RqFake;
import org.takes.rs.RsHeadPrint;

/**
 * Test case for {@link TkFiles}.
 * @since 0.8
 */
final class TkFilesTest {

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void dispatchesByFileName(@TempDir final Path temp) throws Exception {
        final File file = temp.resolve("a.txt").toFile();
        FileUtils.write(
            file, "hello, world!",
            StandardCharsets.UTF_8
        );
        MatcherAssert.assertThat(
            new RsHeadPrint(
                new TkFiles(temp.toFile()).act(
                    new RqFake(
                        "GET", "/a.txt?hash=a1b2c3", ""
                    )
                )
            ),
            new StartsWith("HTTP/1.1 200 OK")
        );
        FileUtils.delete(file);
    }

    @Test
    void throwsWhenResourceNotFound() {
        Assertions.assertThrows(
            HttpException.class,
            () -> new TkFiles("/absent-dir-for-sure").act(
                new RqFake("PUT", "/something-else.txt", "")
            )
        );
    }
}
