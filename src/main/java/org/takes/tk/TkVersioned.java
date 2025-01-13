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

import java.util.ResourceBundle;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Take;
import org.takes.rs.RsWithHeader;

/**
 * Take that adds an HTTP header to each response with a version
 * of Take framework.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.4
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkVersioned extends TkWrap {

    /**
     * Version of the library.
     */
    private static final String VERSION = TkVersioned.make();

    /**
     * Ctor.
     * @param take Original take
     */
    public TkVersioned(final Take take) {
        this(take, "X-Takes-Version");
    }

    /**
     * Ctor.
     * @param take Original take
     * @param header Header to add
     */
    public TkVersioned(final Take take, final String header) {
        super(
            req -> new RsWithHeader(
                take.act(req), header, TkVersioned.VERSION
            )
        );
    }

    /**
     * Make a version.
     * @return Version
     */
    private static String make() {
        final ResourceBundle res =
            ResourceBundle.getBundle("org.takes.version");
        return String.format(
            "%s %s %s",
            res.getString("version"),
            res.getString("revision"),
            res.getString("date")
        );
    }

}
