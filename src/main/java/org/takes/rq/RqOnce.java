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

import java.io.InputStream;
import lombok.EqualsAndHashCode;
import org.cactoos.Scalar;
import org.cactoos.io.InputStreamOf;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.Sticky;
import org.cactoos.text.TextOf;
import org.takes.Request;

/**
 * Request decorator, to prevent multiple calls to {@code body()} method.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.36
 * @todo #1080:30min Please make two decorators (HeadOnce and BodyOnce)
 *  that prevent multiple reads of their contents. Introduce some units
 *  tests for these new classes.
 */
@EqualsAndHashCode(callSuper = true)
public final class RqOnce extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     */
    public RqOnce(final Request req) {
        super(
            new RequestOf(
                new IoChecked<>(new Sticky<>(req::head))::value,
                new IoChecked<>(
                    new Scalar<InputStream>() {
                        private final Scalar<String> text = new Sticky<>(
                            new TextOf(req::body)::asString
                        );

                        @Override
                        public InputStream value() throws Exception {
                            return new InputStreamOf(this.text.value());
                        }
                    }
                )::value
            )
        );
    }
}
