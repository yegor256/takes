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

import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.Scalar;
import org.cactoos.scalar.IoChecked;

/**
 * Take that always fails.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkFailure extends TkWrap {

    /**
     * Ctor.
     */
    public TkFailure() {
        this("Intentional failure");
    }

    /**
     * Ctor.
     * @param err Error to throw
     */
    public TkFailure(final String err) {
        this(new IllegalStateException(err));
    }

    /**
     * Ctor.
     * @param err Error to throw
     */
    public TkFailure(final RuntimeException err) {
        super(
            request -> {
                throw err;
            }
        );
    }

    /**
     * Ctor.
     * @param err Error to throw
     * @since 1.4
     */
    public TkFailure(final Scalar<IOException> err) {
        super(
            request -> {
                throw new IoChecked<>(err).value();
            }
        );
    }

    /**
     * Ctor.
     * @param err Error to throw
     * @since 0.27
     */
    public TkFailure(final IOException err) {
        super(
            request -> {
                throw err;
            }
        );
    }

}
