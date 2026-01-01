/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.takes.facets.auth.Identity;

/**
 * Safe codec decorator that never throws decoding exceptions, providing
 * graceful failure handling by returning anonymous identity.
 *
 * <p>This codec decorator wraps another codec and catches any
 * {@link DecodingException} that might be thrown during decoding operations.
 * When a decoding exception occurs, instead of propagating the exception,
 * it returns {@code Identity.ANONYMOUS}, allowing the application to
 * continue gracefully with an unauthenticated user.
 *
 * <p>This is particularly useful in scenarios where you want to handle
 * corrupted or invalid authentication tokens without breaking the user
 * experience.
 *
 * <p>Usage example:
 * <pre> {@code
 * final Codec codec = new CcSafe(new CcPlain());
 * final byte[] corruptedData = getCorruptedToken();
 * final Identity identity = codec.decode(corruptedData);
 * // identity will be Identity.ANONYMOUS instead of throwing exception
 * }</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.5
 */
@EqualsAndHashCode
public final class CcSafe implements Codec {

    /**
     * Original codec.
     */
    private final Codec origin;

    /**
     * Ctor.
     * @param codec Original codec
     */
    public CcSafe(final Codec codec) {
        this.origin = codec;
    }

    @Override
    public byte[] encode(final Identity identity) throws IOException {
        return this.origin.encode(identity);
    }

    @Override
    public Identity decode(final byte[] bytes) throws IOException {
        Identity identity;
        try {
            identity = this.origin.decode(bytes);
        } catch (final DecodingException ex) {
            identity = Identity.ANONYMOUS;
        }
        return identity;
    }

}
