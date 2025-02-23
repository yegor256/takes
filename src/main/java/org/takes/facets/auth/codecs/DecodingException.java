/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

/**
 * Decoding exception.
 *
 * @since 0.5
 */
public final class DecodingException extends RuntimeException {

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = 0x7529FA781EDA1479L;

    /**
     * Public ctor.
     * @param cause The cause of it
     */
    DecodingException(final String cause) {
        super(cause);
    }

    /**
     * Public ctor.
     * @param cause The cause of it
     */
    DecodingException(final Throwable cause) {
        super(cause);
    }

}
