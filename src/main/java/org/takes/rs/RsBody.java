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
package org.takes.rs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.cactoos.Input;

/**
 * The body of a response used by {@link RsWithBody}.
 *
 * @since 0.32
 */
interface RsBody extends Input {
    /**
     * Gives an {@code InputStream} corresponding to the content of
     * the body.
     * @return The content of the body.
     * @throws IOException in case the content of the body could not
     *  be provided.
     */
    InputStream stream() throws IOException;

    /**
     * Gives the length of the stream.
     * @return The length of the stream.
     * @throws IOException in case the length of the stream could not be
     *  retrieved.
     */
    int length() throws IOException;

    /**
     * Content of a body based on an {@link java.net.URL}.
     * @since 0.32
     */
    final class Url implements RsBody {

        /**
         * The {@link java.net.URL} of the content.
         */
        private final java.net.URL url;

        /**
         * Constructs an {@code URL} with the specified {@link java.net.URL}.
         * @param content The {@link java.net.URL} of the content.
         */
        Url(final java.net.URL content) {
            this.url = content;
        }

        @Override
        public InputStream stream() throws IOException {
            return this.url.openStream();
        }

        @Override
        public int length() throws IOException {
            try (InputStream input = this.url.openStream()) {
                return input.available();
            }
        }
    }

    /**
     * Content of a body based on a byte array.
     * @since 0.32
     */
    final class ByteArray implements RsBody {

        /**
         * The content of the body in a byte array.
         */
        private final byte[] bytes;

        /**
         * Constructs an {@code ByteArray} with the specified byte array.
         * @param content The content of the body.
         */
        ByteArray(final byte[] content) {
            this.bytes = content.clone();
        }

        @Override
        public InputStream stream() {
            return new ByteArrayInputStream(this.bytes);
        }

        @Override
        public int length() {
            return this.bytes.length;
        }
    }

    /**
     * The content of the body based on an {@link InputStream}.
     * @since 0.32
     */
    final class Stream implements RsBody {

        /**
         * The content of the body in an InputStream.
         */
        private final InputStream stream;

        /**
         * The length of the stream.
         */
        private final AtomicInteger length;

        /**
         * Constructs an {@code Stream} with the specified {@link InputStream}.
         * @param input The content of the body as stream.
         */
        Stream(final InputStream input) {
            this.stream = input;
            this.length = new AtomicInteger(-1);
        }

        @Override
        public InputStream stream() throws IOException {
            this.estimate();
            return this.stream;
        }

        @Override
        public int length() throws IOException {
            this.estimate();
            return this.length.get();
        }

        /**
         * Estimates the length of the {@code InputStream}.
         * @throws IOException in case the length could not be estimated.
         */
        private void estimate() throws IOException {
            if (this.length.get() == -1) {
                this.length.compareAndSet(-1, this.stream.available());
            }
        }
    }

    /**
     * Decorator that will store the content of the underlying Body into a
     * temporary File.
     *
     * <p><b>The content of the Body will be stored into a temporary
     * file to be able to read it as many times as we want so use it only
     * for large content, for small content use {@link RsBody.ByteArray}
     * instead.</b>
     *
     * @since 0.32
     */
    final class TempFile implements RsBody {

        /**
         * The temporary file that contains the content of the body.
         */
        private final File file;

        /**
         * The underlying body.
         */
        private final RsBody body;

        /**
         * Constructs a {@code TempFile} with the specified {@link RsBody}.
         * @param content The content of the body to store into a temporary file.
         */
        @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
        TempFile(final RsBody content) {
            this.body = content;
            this.file = new File(
                System.getProperty("java.io.tmpdir"),
                String.format(
                    "%s-%s.tmp",
                    RsBody.TempFile.class.getName(),
                    UUID.randomUUID().toString()
                )
            );
            this.file.deleteOnExit();
        }

        @Override
        public InputStream stream() throws IOException {
            return Files.newInputStream(this.file().toPath());
        }

        @Override
        public int length() throws IOException {
            return (int) this.file().length();
        }

        /**
         * Gives the {@code File} that contains the content of the underlying
         * {@code  Body}.
         * @return The {@code File} in which we stored the content of the
         *  underlying {@code  Body}.
         * @throws IOException In case the content of the underlying
         *  {@code Body} could not be stored into the file.
         */
        private File file() throws IOException {
            synchronized (this.file) {
                if (!this.file.exists()) {
                    this.file.deleteOnExit();
                    try (InputStream content = this.body.stream()) {
                        Files.copy(
                            content,
                            Paths.get(this.file.getAbsolutePath()),
                            StandardCopyOption.REPLACE_EXISTING
                        );
                    }
                }
                return this.file;
            }
        }
    }
}
