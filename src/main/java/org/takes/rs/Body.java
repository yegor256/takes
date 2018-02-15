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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The body of a response used by {@link RsWithBody}.
 *
 * @author Nicolas Filotto (nicolas.filotto@gmail.com)
 * @version $Id$
 * @since 0.32
 */
@SuppressWarnings("PMD.TooManyMethods")
interface Body {
    /**
     * Gives an {@code InputStream} corresponding to the content of
     * the body.
     * @return The content of the body.
     * @throws IOException in case the content of the body could not
     *  be provided.
     */
    InputStream input() throws IOException;

    /**
     * Gives the length of the stream.
     * @return The length of the stream.
     * @throws IOException in case the length of the stream could not be
     *  retrieved.
     */
    int length() throws IOException;

    /**
     * Content of a body based on an {@link java.net.URL}.
     */
    final class Url implements Body {

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
        public InputStream input() throws IOException {
            return this.url.openStream();
        }

        @Override
        public int length() throws IOException {
            try (final InputStream input = this.url.openStream()) {
                return input.available();
            }
        }
    }

    /**
     * Content of a body based on a byte array.
     */
    final class ByteArray implements Body {

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
        public InputStream input() {
            return new ByteArrayInputStream(this.bytes);
        }

        @Override
        public int length() {
            return this.bytes.length;
        }
    }

    /**
     * The content of the body based on an {@link InputStream}.
     */
    final class Stream implements Body {

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
        public InputStream input() throws IOException {
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
     * <p><b>The content of the Body will be stored into a temporary
     * file to be able to read it as many times as we want so use it only
     * for large content, for small content use {@link Body.ByteArray}
     * instead.</b>
     */
    final class TempFile implements Body {

        /**
         * The temporary file that contains the content of the body.
         */
        private final File file;

        /**
         * The underlying body.
         */
        private final Body body;

        /**
         * Constructs a {@code TempFile} with the specified {@link Body}.
         * @param body The content of the body to store into a temporary file.
         */
        TempFile(final Body body) {
            this.body = body;
            this.file = new File(
                System.getProperty("java.io.tmpdir"),
                String.format(
                    "%s-%s.tmp",
                    Body.TempFile.class.getName(),
                    UUID.randomUUID().toString()
                )
            );
        }

        @Override
        public InputStream input() throws IOException {
            return new FileInputStream(this.file());
        }

        @Override
        public int length() throws IOException {
            return (int) this.file().length();
        }

        // Needed to remove the file once the Stream object is no more used.
        // @checkstyle NoFinalizerCheck (2 lines)
        // @checkstyle ProtectedMethodInFinalClassCheck (3 lines)
        @Override
        protected void finalize() throws Throwable {
            try {
                Files.delete(Paths.get(this.file.getAbsolutePath()));
            } finally {
                super.finalize();
            }
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
                    try (final InputStream content = this.body.input()) {
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
