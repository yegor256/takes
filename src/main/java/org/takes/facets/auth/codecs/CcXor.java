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
package org.takes.facets.auth.codecs;

import java.io.IOException;
import java.util.Arrays;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
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

}
