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
