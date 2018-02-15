/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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
package org.takes.rs;

import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.misc.Utf8String;

/**
 * Test case for {@link Body}.
 *
 * @author Nicolas Filotto (nicolas.filotto@gmail.com)
 * @version $Id$
 * @since 0.32
 */
public final class BodyTest {

    /**
     * Body.ByteArray can provide the expected input.
     * @throws Exception If some problem inside.
     */
    @Test
    public void returnsCorrectInputWithByteArray() throws Exception {
        final byte[] bytes =
            new Utf8String("ByteArray returnsCorrectInput!").bytes();
        MatcherAssert.assertThat(
            ByteStreams.toByteArray(new Body.ByteArray(bytes).input()),
            Matchers.equalTo(bytes)
        );
    }

    /**
     * Body.ByteArray can provide the expected length.
     * @throws Exception If some problem inside.
     */
    @Test
    public void returnsCorrectLengthWithByteArray() throws Exception {
        final byte[] bytes =
            new Utf8String("ByteArray returnsCorrectLength!").bytes();
        MatcherAssert.assertThat(
            new Body.ByteArray(bytes).length(),
            Matchers.equalTo(bytes.length)
        );
    }

    /**
     * Body.Stream can provide the expected input.
     * @throws Exception If some problem inside.
     */
    @Test
    public void returnsCorrectInputWithStream() throws Exception {
        final byte[] bytes =
            new Utf8String("Stream returnsCorrectInput!").bytes();
        MatcherAssert.assertThat(
            ByteStreams.toByteArray(
                new Body.Stream(new ByteArrayInputStream(bytes)).input()
            ),
            Matchers.equalTo(bytes)
        );
    }

    /**
     * Body.Stream can provide the expected length.
     * @throws Exception If some problem inside.
     */
    @Test
    public void returnsCorrectLengthWithStream() throws Exception {
        final byte[] bytes =
            new Utf8String("Stream returnsCorrectLength!").bytes();
        MatcherAssert.assertThat(
            new Body.Stream(new ByteArrayInputStream(bytes)).length(),
            Matchers.equalTo(bytes.length)
        );
    }

    /**
     * Body.TemFile can provide the expected input.
     * @throws Exception If some problem inside.
     */
    @Test
    public void returnsCorrectInputWithTemFile() throws Exception {
        final byte[] bytes =
            new Utf8String("TempFile returnsCorrectInput!").bytes();
        MatcherAssert.assertThat(
            ByteStreams.toByteArray(
                new Body.TempFile(new Body.ByteArray(bytes)).input()
            ),
            Matchers.equalTo(bytes)
        );
    }

    /**
     * Body.TemFile can provide the expected length.
     * @throws Exception If some problem inside.
     */
    @Test
    public void returnsCorrectLengthWithTemFile() throws Exception {
        final byte[] bytes =
            new Utf8String("TempFile returnsCorrectLength!").bytes();
        MatcherAssert.assertThat(
            new Body.TempFile(new Body.ByteArray(bytes)).length(),
            Matchers.equalTo(bytes.length)
        );
    }

    /**
     * Body.URL can provide the expected input.
     * @throws Exception If some problem inside.
     */
    @Test
    public void returnsCorrectInputWithUrl() throws Exception {
        final Path file = BodyTest.createTempFile();
        try {
            final byte[] bytes =
                new Utf8String("URL returnsCorrectInput!").bytes();
            try (final InputStream input = new ByteArrayInputStream(bytes)) {
                Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
            }
            try (final InputStream input =
                new Body.Url(file.toUri().toURL()).input()) {
                MatcherAssert.assertThat(
                    ByteStreams.toByteArray(input),
                    Matchers.equalTo(bytes)
                );
            }
        } finally {
            Files.deleteIfExists(file);
        }
    }

    /**
     * Body.URL can provide the expected length.
     * @throws Exception If some problem inside.
     */
    @Test
    public void returnsCorrectLengthWithUrl() throws Exception {
        final Path file = BodyTest.createTempFile();
        try {
            final byte[] bytes =
                new Utf8String("URL returnsCorrectLength!").bytes();
            try (final InputStream input = new ByteArrayInputStream(bytes)) {
                Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
            }
            MatcherAssert.assertThat(
                new Body.Url(file.toUri().toURL()).length(),
                Matchers.equalTo(bytes.length)
            );
        } finally {
            Files.deleteIfExists(file);
        }
    }

    /**
     * Creates a temporary file for testing purpose.
     * @return A temporary file for the test.
     * @throws IOException If the file could not be created
     */
    private static Path createTempFile() throws IOException {
        return Files.createTempFile(BodyTest.class.getName(), "tmp");
    }
}
