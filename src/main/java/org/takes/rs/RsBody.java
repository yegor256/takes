/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
import org.cactoos.io.InputOf;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.ScalarOf;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;

/**
 * Interface for response body content used by {@link RsWithBody}.
 *
 * <p>This interface provides abstraction over different types of response
 * body sources including URLs, byte arrays, input streams, and temporary files.
 * Each implementation handles content length calculation and stream provision
 * according to its specific source type. The interface extends Input to
 * provide Cactoos integration.
 *
 * <p>Implementations include:
 * <ul>
 * <li>Url - content from URL sources</li>
 * <li>ByteArray - content from byte arrays</li>
 * <li>Stream - content from input streams</li>
 * <li>TempFile - content stored in temporary files for large data</li>
 * </ul>
 *
 * @since 0.32
 */
interface RsBody extends Input {

    @Override
    InputStream stream() throws IOException;

    /**
     * Gives the length of the stream.
     * @return The length of the stream
     * @throws IOException in case the length of the stream could not be
     *  retrieved
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
        private final java.net.URL source;

        /**
         * Constructs an {@code URL} with the specified {@link java.net.URL}.
         * @param content The {@link java.net.URL} of the content
         */
        Url(final java.net.URL content) {
            this.source = content;
        }

        @Override
        public InputStream stream() throws IOException {
            return this.source.openStream();
        }

        @Override
        @SuppressWarnings("PMD.UnnecessaryLocalRule")
        public int length() throws IOException {
            try (InputStream input = this.source.openStream()) {
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
        private final Unchecked<byte[]> bytes;

        /**
         * Constructs an {@code ByteArray} with the specified byte array.
         * @param content The content of the body
         */
        ByteArray(final byte[] content) {
            this.bytes = new Unchecked<>(
                new Sticky<>(content::clone)
            );
        }

        @Override
        public InputStream stream() {
            return new ByteArrayInputStream(this.bytes.value());
        }

        @Override
        public int length() {
            return this.bytes.value().length;
        }
    }

    /**
     * Content of a body based on a CharSequence and a Charset.
     * @since 2.0
     */
    final class Text implements RsBody {

        /**
         * The content of the body as text.
         */
        private final CharSequence content;

        /**
         * Charset used to encode the text.
         */
        private final java.nio.charset.Charset charset;

        /**
         * Ctor.
         * @param body The content of the body
         * @param chr Charset to encode with
         */
        Text(final CharSequence body, final java.nio.charset.Charset chr) {
            this.content = body;
            this.charset = chr;
        }

        @Override
        public InputStream stream() {
            return new ByteArrayInputStream(this.bytes());
        }

        @Override
        public int length() {
            return this.bytes().length;
        }

        /**
         * Encode the text into bytes.
         * @return Bytes
         */
        private byte[] bytes() {
            if (this.content == null) {
                throw new IllegalStateException(
                    "Body content is null, cannot encode to bytes"
                );
            }
            return this.content.toString().getBytes(this.charset);
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
        private final InputStream input;

        /**
         * The length of the stream.
         */
        private final AtomicInteger len;

        /**
         * Constructs an {@code Stream} with the specified {@link InputStream}.
         * @param input The content of the body as stream
         */
        Stream(final InputStream input) {
            this.input = input;
            this.len = new AtomicInteger(-1);
        }

        @Override
        public InputStream stream() throws IOException {
            this.estimate();
            return this.input;
        }

        @Override
        public int length() throws IOException {
            this.estimate();
            return this.len.get();
        }

        /**
         * Estimates the length of the {@code InputStream}.
         * @throws IOException in case the length could not be estimated
         */
        private void estimate() throws IOException {
            if (this.len.get() == -1) {
                this.len.compareAndSet(-1, this.input.available());
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
        private final Unchecked<File> tmp;

        /**
         * The underlying body.
         */
        private final RsBody body;

        /**
         * Constructs a {@code TempFile} with the specified {@link RsBody}.
         * @param content The content of the body to store into a temporary file
         */
        TempFile(final RsBody content) {
            this.body = content;
            this.tmp = new Unchecked<>(
                new Sticky<>(
                    () -> {
                        final File file = new File(
                            System.getProperty("java.io.tmpdir"),
                            String.format(
                                "%s-%s.tmp",
                                RsBody.TempFile.class.getName(),
                                UUID.randomUUID().toString()
                            )
                        );
                        file.deleteOnExit();
                        return file;
                    }
                )
            );
        }

        @Override
        public InputStream stream() throws IOException {
            return new IoChecked<>(
                new ScalarOf<>(
                    () -> new InputOf(this.file()).stream()
                )
            ).value();
        }

        @Override
        public int length() throws IOException {
            return (int) this.file().length();
        }

        /**
         * Gives the {@code File} that contains the content of the underlying
         * {@code  Body}.
         * @return The {@code File} in which we stored the content of the
         *  underlying {@code  Body}
         * @throws IOException In case the content of the underlying
         *  {@code Body} could not be stored into the file
         */
        @SuppressWarnings(
            {"PMD.AvoidSynchronizedStatement", "PMD.UnnecessaryLocalRule"}
        )
        private File file() throws IOException {
            final File file = this.tmp.value();
            synchronized (file) {
                if (!file.exists()) {
                    file.deleteOnExit();
                    try (InputStream content = this.body.stream()) {
                        Files.copy(
                            content,
                            Paths.get(file.getAbsolutePath()),
                            StandardCopyOption.REPLACE_EXISTING
                        );
                    }
                }
                return file;
            }
        }
    }
}
