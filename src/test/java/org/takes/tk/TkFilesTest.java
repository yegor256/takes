/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
