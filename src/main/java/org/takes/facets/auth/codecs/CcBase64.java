/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import lombok.EqualsAndHashCode;
import org.takes.facets.auth.Identity;

/**
 * Base64 codec.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.13
 */
@EqualsAndHashCode
public final class CcBase64 implements Codec {

    /**
     * All legal Base64 chars.
     */
    private static final String BASE64CHARS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    /**
     * Original codec.
     */
    private final Codec origin;

    /**
     * Ctor.
     * @param codec Original codec
     */
    public CcBase64(final Codec codec) {
        this.origin = codec;
    }

    @Override
    public byte[] encode(final Identity identity) throws IOException {
        return Base64.getEncoder().encode(this.origin.encode(identity));
    }

    @Override
    public Identity decode(final byte[] bytes) throws IOException {
        final byte[] illegal = CcBase64.checkIllegalCharacters(bytes);
        if (illegal.length > 0) {
            throw new DecodingException(
                String.format(
                    "Illegal character in Base64 encoded data. %s",
                    Arrays.toString(illegal)
                    )
                );
        }
        return this.origin.decode(Base64.getDecoder().decode(bytes));
    }

    /**
     * Check the byte array for non-Base64 characters.
     *
     * @param bytes The values to check
     * @return An array of the found non-Base64 characters.
     */
    private static byte[] checkIllegalCharacters(final byte[] bytes) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (final byte byt: bytes) {
            if (BASE64CHARS.indexOf(byt) < 0) {
                out.write(byt);
            }
        }
        return out.toByteArray();
    }
}
