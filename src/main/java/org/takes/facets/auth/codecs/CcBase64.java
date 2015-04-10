/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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
import org.takes.facets.auth.Identity;

/**
 * Base64 codec.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Igor Khvostenkov (ikhvostenkov@gmail.com)
 * @version $Id$
 * @since 0.13
 */
@EqualsAndHashCode
public final class CcBase64 implements Codec {
    /**
     * Base64 alphabet table.
     */
    private static final byte[] ENCODE_TABLE = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };
    /**
     * This array is a lookup table that translates Unicode characters drawn
     * from the "Base64 Alphabet" into their 6-bit positive integer equivalents.
     * Characters that are not in the Base64 alphabet but fall within the bounds
     * of the array are translated to -1.
     */
    private static final byte[] DECODE_TABLE = {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54,
        55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4,
        5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
        24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34,
        35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51
    };
    /**
     * BASE64 characters are 6 bits in length.
     * They are formed by taking a block of 3 octets to form a 24-bit string,
     * which is converted into 4 BASE64 characters.
     */
    private static final int BITS_PER_ENCODED_BYTE = 6;
    /**
     * Number of octets in block.
     */
    private static final int BYTES_PER_UNENCODED_BLOCK = 3;
    /**
     * BASE64 characters in block.
     */
    private static final int BYTES_PER_ENCODED_BLOCK = 4;
    /**
     * Buffer resize factor.
     */
    private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;
    /**
     * Mask used to extract 6 bits
     */
    private static final int MASK_6BITS = 0x3f;
    /**
     * Mask used to extract 8 bits.
     */
    private static final int MASK_8BITS = 0xff;
    /**
     * Bit shift.
     */
    private static final int BIT_SHIFT = 0xf;
    /**
     * Padding byte.
     */
    private static final byte PADDING_CHAR = 0x3D;
    /**
     * Original codec.
     */
    private final transient Codec origin;
    /**
     * Ctor.
     * @param codec Original codec
     */
    public CcBase64(final Codec codec) {
        this.origin = codec;
    }

    @Override
    public byte[] encode(final Identity identity) throws IOException {
        byte[] raw = this.origin.encode(identity);
        final int rawLength = raw.length;
        if (rawLength == 0){
            return raw;
        }
        final int noPadOutLength = (rawLength * BYTES_PER_ENCODED_BLOCK
            + DEFAULT_BUFFER_RESIZE_FACTOR) / BYTES_PER_UNENCODED_BLOCK;
        final int resultLength = ((rawLength + DEFAULT_BUFFER_RESIZE_FACTOR)
            / BYTES_PER_UNENCODED_BLOCK) * BYTES_PER_ENCODED_BLOCK;
        byte[] result = new byte[resultLength];
        int inputPos = 0;
        int outputPos = 0;
        while (inputPos < rawLength) {
            final int firstOctet = raw[inputPos++] & MASK_8BITS;
            final int secondOctet = inputPos < rawLength ?
                raw[inputPos++] & MASK_8BITS : 0;
            final int thirdOctet = inputPos < rawLength ?
                raw[inputPos++] & MASK_8BITS : 0;
            result[outputPos++] = ENCODE_TABLE[firstOctet >>> 2];
            result[outputPos++] = ENCODE_TABLE[((firstOctet
                & BYTES_PER_UNENCODED_BLOCK) << 4)
                | (secondOctet >>> BYTES_PER_ENCODED_BLOCK)];
            result[outputPos] = outputPos < noPadOutLength ?
                ENCODE_TABLE[((secondOctet & BIT_SHIFT) << 2) |
                (thirdOctet >>> BITS_PER_ENCODED_BYTE)] : PADDING_CHAR;
            outputPos++;
            result[outputPos] = outputPos < noPadOutLength ?
                ENCODE_TABLE[thirdOctet & MASK_6BITS] : PADDING_CHAR;
            outputPos++;
        }
        return result;
    }

    @Override
    public Identity decode(final byte[] bytes) throws IOException {
        int inputLength = bytes.length;
        if (inputLength % 4 != 0)
            throw new DecodingException(
                String.format(
                    "Length of Base64 encoded input must be multiple of 4: %s",
                    Arrays.toString(bytes)
                )
            );
        while (inputLength > 0 && bytes[inputLength - 1] == PADDING_CHAR)
            inputLength--;
        int resultLength = (inputLength * BYTES_PER_UNENCODED_BLOCK)
            / BYTES_PER_ENCODED_BLOCK;
        byte[] out = new byte[resultLength];
        int inputPos = 0;
        int outputPos = 0;
        while (inputPos < inputLength) {
            final int firstBlock = DECODE_TABLE[bytes[inputPos++]];
            final int secondBlock = DECODE_TABLE[bytes[inputPos++]];
            final int thirdBlock = DECODE_TABLE[inputPos < inputLength ?
                bytes[inputPos++] : 'A'];
            final int forthBlock = DECODE_TABLE[inputPos < inputLength ?
                bytes[inputPos++] : 'A'];
            if (firstBlock < 0 || secondBlock < 0 || thirdBlock < 0
                || forthBlock < 0)
                throw new DecodingException(
                    String.format(
                        "Illegal character in Base64 encoded data. %s",
                        Arrays.toString(bytes)
                    )
                );
            int firstOctet = (firstBlock << 2) | (secondBlock >>> 4);
            int secondOctet = ((secondBlock & 0xf) << 4) | (thirdBlock >>> 2);
            int thirdOctet = ((thirdBlock & 3) << 6) | forthBlock;
            out[outputPos++] = (byte) firstOctet;
            if (outputPos < resultLength)
                out[outputPos++] = (byte) secondOctet;
            if (outputPos < resultLength)
                out[outputPos++] = (byte) thirdOctet;
        }
        return this.origin.decode(out);
    }
}
