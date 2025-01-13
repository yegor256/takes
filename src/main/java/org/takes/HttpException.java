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
