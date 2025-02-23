/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.io.InputStream;
import java.net.URL;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.Scalar;
import org.takes.rs.RsHtml;

/**
 * HTML take.
 *
 * <p>This take returns an HTML response by wrapping the provided
 * content into {@link org.takes.rs.RsHtml}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkHtml extends TkWrap {

    /**
     * Ctor.
     * @param body Text
     */
    public TkHtml(final String body) {
        super(
            req -> new RsHtml(body)
        );
    }

    /**
     * Ctor.
     * @param body Text
     * @since 1.4
     */
    public TkHtml(final Scalar<String> body) {
        super(
            req -> new RsHtml(body.value())
        );
    }

    /**
     * Ctor.
     * @param body Body with HTML
     */
    public TkHtml(final byte[] body) {
        super(
            req -> new RsHtml(body)
        );
    }

    /**
     * Ctor.
     * @param url URL with content
     */
    public TkHtml(final URL url) {
        super(
            req -> new RsHtml(url)
        );
    }

    /**
     * Ctor.
     * @param body Content
     */
    public TkHtml(final InputStream body) {
        super(
            req -> new RsHtml(body)
        );
    }

}
