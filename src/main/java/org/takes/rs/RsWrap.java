/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import java.io.InputStream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;

/**
 * Response decorator.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(of = "origin")
@EqualsAndHashCode
public class RsWrap implements Response {

    /**
     * Original response.
     */
    private final Response origin;

    /**
     * Ctor.
     * @param res Original response
     */
    public RsWrap(final Response res) {
        this.origin = res;
    }

    @Override
    public final Iterable<String> head() throws IOException {
        return this.origin.head();
    }

    @Override
    public final InputStream body() throws IOException {
        return this.origin.body();
    }
}
