/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.cactoos.io.InputStreamOf;
import org.takes.Request;
import org.takes.rq.RqLive;
import org.takes.rq.RqWithHeader;
import org.takes.rq.TempInputStream;

/**
 * Lazily-built file-backed request.
 * @since 2.0
 */
final class LazyRq implements Request {

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

    private Request delegate() throws IOException {
        if (this.cached == null) {
            this.cached = new RqWithHeader(
                new RqLive(
                    new TempInputStream(
                        new InputStreamOf(this.file),
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
