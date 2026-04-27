/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.rq.RqLive;
import org.takes.rq.RqWithHeader;
import org.takes.rq.RqWrap;
import org.takes.rq.TempInputStream;

/**
 * Request with a temporary file as body. The temporary file will be deleted
 * automatically when the body of the request will be closed.
 * @see org.takes.rq.RqLive
 * @see org.takes.rq.TempInputStream
 * @since 0.33
 */
@EqualsAndHashCode(callSuper = true)
final class RqTemp extends RqWrap {

    /**
     * Creates a {@code RqTemp} with the specified temporary file.
     * @param file The temporary that will be automatically deleted when the
     *  body of the request will be closed
     * @throws IOException If fails
     */
    RqTemp(final File file) throws IOException {
        super(new RqTemp.LazyRq(file));
    }

    /**
     * Lazily-built file-backed request.
     * @since 2.0
     */
    private static final class LazyRq implements Request {

        /**
         * Source temporary file.
         */
        private final File file;

        /**
         * Cached decorated request.
         */
        private Request cached;

        /**
         * Ctor.
         * @param src Source file
         */
        LazyRq(final File src) {
            this.file = src;
        }

        @Override
        public Iterable<String> head() throws IOException {
            return this.delegate().head();
        }

        @Override
        public InputStream body() throws IOException {
            return this.delegate().body();
        }

        /**
         * Build the delegate once.
         * @return Decorated request
         * @throws IOException If fails
         */
        private Request delegate() throws IOException {
            if (this.cached == null) {
                this.cached = new RqWithHeader(
                    new RqLive(
                        new TempInputStream(
                            Files.newInputStream(this.file.toPath()),
                            this.file
                        )
                    ),
                    "Content-Length",
                    String.valueOf(this.file.length())
                );
            }
            return this.cached;
        }
    }
}
