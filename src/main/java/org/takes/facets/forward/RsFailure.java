/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.forward;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import org.takes.facets.flash.RsFlash;

/**
 * A failure response that combines redirect and flash message functionality.
 *
 * <p>This class extends {@link RsForward} to provide a convenient way to redirect
 * users while displaying error messages. It combines the redirect capabilities of
 * {@link org.takes.facets.forward.RsForward} with the flash message functionality
 * of {@link org.takes.facets.flash.RsFlash}, making it ideal for error handling
 * scenarios. The class is immutable and thread-safe.
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
     * Constructor with throwable cause and default home location.
     * @param cause The throwable that caused the failure
     * @throws UnsupportedEncodingException If URL encoding fails
     */
    public RsFailure(final Throwable cause)
        throws UnsupportedEncodingException {
        this(cause, RsFailure.HOME);
    }

    /**
     * Constructor with throwable cause and custom redirect location.
     * @param cause The throwable that caused the failure
     * @param loc The location URL to redirect to
     * @throws UnsupportedEncodingException If URL encoding fails
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
     * Constructor with string cause and default home location.
     * @param cause The error message that caused the failure
     * @throws UnsupportedEncodingException If URL encoding fails
     */
    public RsFailure(final String cause)
        throws UnsupportedEncodingException {
        this(cause, RsFailure.HOME);
    }

    /**
     * Constructor with string cause and custom redirect location.
     * @param cause The error message that caused the failure
     * @param loc The location URL to redirect to
     * @throws UnsupportedEncodingException If URL encoding fails
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
