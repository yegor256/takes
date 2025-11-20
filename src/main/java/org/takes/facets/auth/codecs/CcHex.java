/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.takes.facets.auth.Identity;

/**
 * Hexadecimal codec that encodes identity data as hexadecimal strings
 * with optional hyphen separators for improved readability.
 *
 * <p>This codec decorator converts binary data to hexadecimal representation
 * using uppercase letters (A-F) for digits 10-15. It automatically inserts
 * hyphens every 4 bytes (8 hex characters) to improve readability of long
 * hex strings, similar to UUID formatting.
 *
 * <p>The format produces strings like: {@code 48656C6C-6F20576F-726C6421}
 * where hyphens separate every 4 bytes of the original data.
 *
 * <p>Usage example:
 * <pre> {@code
 * final Codec codec = new CcHex(new CcPlain());
 * final Identity identity = new Identity.Simple("urn:user:john", props);
 * final byte[] encoded = codec.encode(identity); // hex-encoded with hyphens
 * final Identity decoded = codec.decode(encoded); // hyphen-aware decoding
 * }</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode
public final class CcHex implements Codec {

    /**
     * Length of chunk.
     */
    private static final int CHUNK = 4;

    /**
     * Backward mapping table.
     */
    private static final byte[] BACK = {
        -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1,
        0, 1, 2, 3, 4, 5, 6, 7,
        8, 9, -1, -1, -1, -1, -1, -1,
        -1, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf,
    };

    /**
     * Forward mapping table.
     */
    private static final byte[] FWD = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
    };

    /**
     * Original codec.
     */
    private final Codec origin;

    /**
     * Ctor.
     * @param codec Original codec
     */
    public CcHex(final Codec codec) {
        this.origin = codec;
    }

    @Override
    public byte[] encode(final Identity identity) throws IOException {
        final byte[] raw = this.origin.encode(identity);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int idx = 0; idx < raw.length; ++idx) {
            if (idx > 0 && idx % CcHex.CHUNK == 0) {
                out.write('-');
            }
            out.write(CcHex.FWD[raw[idx] >> 4 & 0x0f]);
            out.write(CcHex.FWD[raw[idx] & 0x0f]);
        }
        return out.toByteArray();
    }

    @Override
    @SuppressWarnings("PMD.AvoidAccessToStaticMembersViaThis")
    public Identity decode(final byte[] bytes) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        int idx = 0;
        while (idx < bytes.length) {
            if (bytes[idx] == '-') {
                ++idx;
                continue;
            }
            if (idx > bytes.length - 2) {
                throw new DecodingException("not enough data");
            }
            out.write(
                (CcHex.decode(bytes[idx]) << 4) + CcHex.decode(bytes[idx + 1])
            );
            idx += 2;
        }
        return this.origin.decode(out.toByteArray());
    }

    /**
     * Convert hex to number.
     * @param hex Hex number
     * @return Decoded
     */
    private static int decode(final int hex) {
        if (hex >= CcHex.BACK.length) {
            throw new DecodingException(
                String.format("invalid hex char: 0x%2x", hex)
            );
        }
        final int dec = CcHex.BACK[hex];
        if (dec < 0) {
            throw new DecodingException(
                String.format("invalid hex character: 0x%2x", hex)
            );
        }
        return dec;
    }

}
