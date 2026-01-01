/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
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
 * Base64 codec that encodes identity data using standard Base64 encoding.
 *
 * <p>This codec decorator applies Base64 encoding to make binary data
 * safe for transmission over text-based protocols. It wraps another codec
 * and converts its binary output to Base64-encoded strings, which can be
 * safely transmitted via HTTP headers, URLs, or stored in text formats.
 *
 * <p>During decoding, it validates that all input characters are legal
 * Base64 characters before attempting to decode, throwing a
 * {@link DecodingException} if illegal characters are found.
 *
 * <p>Usage example:
 * <pre> {@code
 * final Codec codec = new CcBase64(new CcCompact());
 * final Identity identity = new Identity.Simple("urn:user:john", props);
 * final byte[] encoded = codec.encode(identity); // Base64-encoded
 * final Identity decoded = codec.decode(encoded); // validated and decoded
 * }</pre>
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
        for (final byte the: bytes) {
            if (CcBase64.BASE64CHARS.indexOf(the) < 0) {
                out.write(the);
            }
        }
        return out.toByteArray();
    }
}
