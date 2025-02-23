/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.takes.facets.auth.Identity;

/**
 * Safe codec, never throws decoding exception.
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
