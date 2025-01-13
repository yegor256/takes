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
package org.takes.rq;

import lombok.EqualsAndHashCode;
import org.cactoos.iterable.Filtered;
import org.cactoos.scalar.Not;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Lowered;
import org.cactoos.text.StartsWith;
import org.takes.Request;

/**
 * Request without a header (even if it was absent).
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.8
 */
@EqualsAndHashCode(callSuper = true)
public final class RqWithoutHeader extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     * @param name Header name
     */
    public RqWithoutHeader(final Request req, final CharSequence name) {
        super(
            // @checkstyle AnonInnerLengthCheck (50 lines)
            new RequestOf(
                () -> new Filtered<>(
                    header -> new Not(
                        new StartsWith(
                            new Lowered(header),
                            new FormattedText(
                                "%s:",
                                new Lowered(name.toString())
                            )
                        )
                    ).value(),
                    req.head()
                ),
                req::body
            )
        );
    }

}
