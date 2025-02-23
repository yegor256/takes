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
 * Hex codec.
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
