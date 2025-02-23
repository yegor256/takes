/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
