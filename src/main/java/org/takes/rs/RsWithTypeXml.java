/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.nio.charset.Charset;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Response decorator, with content type text/xml.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.30
 */
public final class RsWithTypeXml extends RsWrap {

    /**
     * Constructs a {@code XML} that will add text/xml as the content type
     * header to the response.
     * @param res Original response
     */
    public RsWithTypeXml(final Response res) {
        this(res, new Opt.Empty<>());
    }

    /**
     * Constructs a {@code XML} that will add text/xml as the content type
     * header to the response using the specified charset as charset
     * parameter value.
     * @param res Original response
     * @param charset The character set to add in the content type header
     */
    public RsWithTypeXml(final Response res, final Charset charset) {
        this(res, new Opt.Single<>(charset));
    }

    /**
     * Constructs a {@code XML} that will add text/xml as the content type
     * header to the response using the specified charset as charset
     * parameter value if present.
     * @param res Original response
     * @param charset The character set to add in the content type header if
     *  present
     */
    RsWithTypeXml(final Response res, final Opt<Charset> charset) {
        super(new LazyRs(res, "text/xml", charset));
    }
}
