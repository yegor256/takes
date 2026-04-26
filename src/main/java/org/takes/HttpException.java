/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes;

import java.io.IOException;

/**
 * HTTP-aware exception.
 * @since 0.13
 */
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
     * Detail message attached to the status (may be null).
     */
    private final String detail;

    /**
     * Ctor.
     * @param code HTTP status code
     */
    public HttpException(final int code) {
        this(code, (String) null);
    }

    /**
     * Ctor.
     * @param code HTTP status code
     * @param cause Cause of the problem
     */
    public HttpException(final int code, final String cause) {
        this(code, cause, null);
    }

    /**
     * Ctor.
     * @param code HTTP status code
     * @param cause Cause of the problem
     */
    public HttpException(final int code, final Throwable cause) {
        this(code, null, cause);
    }

    /**
     * Primary ctor.
     * @param code HTTP status code
     * @param msg Exception message
     * @param cause Cause of the problem
     */
    public HttpException(final int code, final String msg,
        final Throwable cause) {
        super(cause);
        this.status = code;
        this.detail = msg;
    }

    // @checkstyle DesignForExtensionCheck (10 lines)
    @Override
    public String getMessage() {
        final String msg;
        if (this.detail == null) {
            msg = Integer.toString(this.status);
        } else {
            msg = String.format(
                HttpException.MESSAGE_FORMAT, this.status, this.detail
            );
        }
        return msg;
    }

    /**
     * HTTP status code.
     * @return Code
     */
    public final int code() {
        return this.status;
    }
}
