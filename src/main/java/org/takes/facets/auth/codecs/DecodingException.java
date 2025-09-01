/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

/**
 * Exception thrown when identity decoding fails due to invalid or corrupted data.
 *
 * <p>This runtime exception is specifically thrown by codec implementations
 * when they encounter data that cannot be properly decoded back into an
 * Identity object. This typically occurs when the input data is corrupted,
 * malformed, or was encoded with different parameters than those used for
 * decoding.
 *
 * <p>Usage example:
 * <pre> {@code
 * try {
 *     final Identity identity = codec.decode(corruptedBytes);
 * } catch (final DecodingException ex) {
 *     // Handle decoding failure, possibly return Anonymous identity
 *     return Identity.ANONYMOUS;
 * }
 * }</pre>
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
