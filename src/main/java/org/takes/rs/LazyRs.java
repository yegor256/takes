/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Lazily-built typed response.
 * @since 2.0
 */
final class LazyRs implements Response {

    /**
     * Original response.
     */
    private final Response res;

    /**
     * Content type.
     */
    private final CharSequence type;

    /**
     * Optional charset.
     */
    private final Opt<Charset> charset;

    /**
     * Ctor.
     * @param origin Original response
     * @param ctype Content type
     * @param chr Optional charset
     */
    LazyRs(final Response origin, final CharSequence ctype,
        final Opt<Charset> chr) {
        this.res = origin;
        this.type = ctype;
        this.charset = chr;
    }

    @Override
    public Iterable<String> head() throws IOException {
        return RsWithType.make(this.res, this.type, this.charset).head();
    }

    @Override
    public InputStream body() throws IOException {
        return RsWithType.make(this.res, this.type, this.charset).body();
    }
}
