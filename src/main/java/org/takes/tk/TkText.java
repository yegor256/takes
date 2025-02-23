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
import org.takes.rs.RsText;

/**
 * Text take.
 *
 * <p>This take returns an HTML response by wrapping the provided
 * content into {@link RsText}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkText extends TkWrap {

    /**
     * Ctor.
     * @since 0.9
     */
    public TkText() {
        this("");
    }

    /**
     * Ctor.
     * @param body Text
     */
    public TkText(final String body) {
        super(
            req -> new RsText(body)
        );
    }

    /**
     * Ctor.
     * @param body Text
     * @since 1.4
     */
    public TkText(final Scalar<String> body) {
        super(
            req -> new RsText(body.value())
        );
    }

    /**
     * Ctor.
     * @param body Body with HTML
     */
    public TkText(final byte[] body) {
        super(
            req -> new RsText(body)
        );
    }

    /**
     * Ctor.
     * @param url URL with content
     */
    public TkText(final URL url) {
        super(
            req -> new RsText(url)
        );
    }

    /**
     * Ctor.
     * @param body Content
     */
    public TkText(final InputStream body) {
        super(
            req -> new RsText(body)
        );
    }

}
