/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes;

import java.io.IOException;

/**
 * HTTP-aware exception.
 *
 * @since 0.13
 */
@SuppressWarnings("PMD.OnlyOneConstructorShouldDoInitialization")
public class HttpException extends IOException {

    /**
     * Message format.
     */
    private static final String MESSAGE_FORMAT = "[%03d] %s";

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = -505306086879848229L;

    /**
     * Status code.
     */
    private final int status;

    /**
     * Ctor.
     * @param code HTTP status code
     */
    public HttpException(final int code) {
        super(Integer.toString(code));
        this.status = code;
    }

    /**
     * Ctor.
     * @param code HTTP status code
     * @param cause Cause of the problem
     */
    public HttpException(final int code, final String cause) {
        super(String.format(HttpException.MESSAGE_FORMAT, code, cause));
        this.status = code;
    }

    /**
     * Ctor.
     * @param code HTTP status code
     * @param cause Cause of the problem
     */
    public HttpException(final int code, final Throwable cause) {
        super(cause);
        this.status = code;
    }

    /**
     * Ctor.
     * @param code HTTP status code
     * @param msg Exception message
     * @param cause Cause of the problem
     */
    public HttpException(final int code, final String msg,
        final Throwable cause) {
        super(String.format(HttpException.MESSAGE_FORMAT, code, msg), cause);
        this.status = code;
    }

    /**
     * HTTP status code.
     * @return Code
     */
    public final int code() {
        return this.status;
    }

}
