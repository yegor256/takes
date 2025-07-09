/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.takes.facets.auth.Identity;

/**
 * XOR codec.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
public final class CcXor implements Codec {

    /**
     * Original codec.
     */
    private final Codec origin;

    /**
     * Secret to use for encoding.
     */
    private final byte[] secret;

    /**
     * Ctor.
     * @param codec Original codec
     * @param key Secret key for encoding
     */
    public CcXor(final Codec codec, final String key) {
        this(
            codec,
            new UncheckedBytes(new BytesOf(key)).asBytes()
        );
    }

    /**
     * Ctor.
     * @param codec Original codec
     * @param key Secret key for encoding
     */
    public CcXor(final Codec codec, final byte[] key) {
        this.origin = codec;
        this.secret = Arrays.copyOf(key, key.length);
    }

    @Override
    public byte[] encode(final Identity identity) throws IOException {
        return this.xor(this.origin.encode(identity));
    }

    @Override
    public Identity decode(final byte[] bytes) throws IOException {
        return this.origin.decode(this.xor(bytes));
    }

    /**
     * XOR array of bytes.
     * @param input The input to XOR
     * @return Encrypted output
     */
    private byte[] xor(final byte[] input) {
        final byte[] output = new byte[input.length];
        if (this.secret.length == 0) {
            System.arraycopy(input, 0, output, 0, input.length);
        } else {
            int spos = 0;
            for (int pos = 0; pos < input.length; ++pos) {
                output[pos] = (byte) (input[pos] ^ this.secret[spos]);
                ++spos;
                if (spos >= this.secret.length) {
                    spos = 0;
                }
            }
        }
        return output;
    }

    /**
     * Check for equality.
     *
     * @param o The object to check
     * @return True if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CcXor ccXor = (CcXor) o;
        return Objects.equals(origin, ccXor.origin)
                && Objects.deepEquals(secret, ccXor.secret);
    }

    /**
     * Calculate hash code.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(origin, Arrays.hashCode(secret));
    }

}
