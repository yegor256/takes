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
package org.takes.facets.forward;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import org.takes.facets.flash.RsFlash;

/**
 * Failure (combination of {@link org.takes.facets.forward.RsForward}
 * and {@link org.takes.facets.flash.RsFlash}).
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.18
 */
@EqualsAndHashCode(callSuper = true)
public final class RsFailure extends RsForward {

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = 396488574468386488L;

    /**
     * Home page, by default.
     */
    private static final String HOME = "/";

    /**
     * Ctor.
     * @param cause Cause
     * @throws UnsupportedEncodingException If fails
     */
    public RsFailure(final Throwable cause)
        throws UnsupportedEncodingException {
        this(cause, RsFailure.HOME);
    }

    /**
     * Ctor.
     * @param cause Cause
     * @param loc Location to redirect to
     * @throws UnsupportedEncodingException If fails
     * @since 0.21
     */
    public RsFailure(final Throwable cause, final CharSequence loc)
        throws UnsupportedEncodingException {
        super(
            new RsFlash(cause),
            HttpURLConnection.HTTP_MOVED_PERM,
            loc
        );
    }

    /**
     * Ctor.
     * @param cause Cause
     * @throws UnsupportedEncodingException If fails
     */
    public RsFailure(final String cause)
        throws UnsupportedEncodingException {
        this(cause, RsFailure.HOME);
    }

    /**
     * Ctor.
     * @param cause Cause
     * @param loc Location to redirect to
     * @throws UnsupportedEncodingException If fails
     * @since 0.21
     */
    public RsFailure(final String cause, final CharSequence loc)
        throws UnsupportedEncodingException {
        super(
            new RsFlash(cause),
            HttpURLConnection.HTTP_MOVED_PERM,
            loc
        );
    }

}
