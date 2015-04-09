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
 * @since 0.11
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
    private static final int ENCOD_BYTE_LEN = 6;
    /**
     * Number of octets in block.
     */
    private static final int UNENCOD_BLOCK_LEN = 3;
    /**
     * BASE64 characters in block.
     */
    private static final int ENCOD_BLOCK_LEN = 4;
    /**
     * Buffer resize factor.
     */
    private static final int RESIZE_FACTOR = 2;
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
     * Input byte array position.
     */
    private transient int inputPos;
    /**
     * Ctor.
     * @param codec Original codec
     */
    public CcBase64(final Codec codec) {
        this.origin = codec;
    }

    @Override
    public byte[] encode(final Identity identity) throws IOException {
        final byte[] raw = this.origin.encode(identity);
        final int rawLength = raw.length;
        byte[] result;
        if (rawLength == 0){
            result = raw;
        } else {
            final int noPadOutLength = (rawLength * ENCOD_BLOCK_LEN
                + RESIZE_FACTOR) / UNENCOD_BLOCK_LEN;
            final int resultLength = ((rawLength + RESIZE_FACTOR)
                / UNENCOD_BLOCK_LEN) * ENCOD_BLOCK_LEN;
            result = new byte[resultLength];
            inputPos = 0;
            int outputPos = 0;
            while (inputPos < rawLength) {
                final int firstOctet = this.calcEncOctet(1, raw);
                final int secondOctet = this.calcEncOctet(2, raw);
                final int thirdOctet = this.calcEncOctet(3, raw);
                result[outputPos++] = ENCODE_TABLE[firstOctet >>> 2];
                result[outputPos++] = ENCODE_TABLE[((firstOctet
                    & UNENCOD_BLOCK_LEN) << 4)
                    | (secondOctet >>> ENCOD_BLOCK_LEN)];
                result[outputPos] = outputPos < noPadOutLength
                    ? ENCODE_TABLE[((secondOctet & BIT_SHIFT) << 2)
                    | (thirdOctet >>> ENCOD_BYTE_LEN)] : PADDING_CHAR;
                outputPos++;
                result[outputPos] = outputPos < noPadOutLength
                    ? ENCODE_TABLE[thirdOctet & MASK_6BITS] : PADDING_CHAR;
                outputPos++;
            }
        }
        return result;
    }

    @Override
    public Identity decode(final byte[] bytes) throws IOException {
        int inputLength = bytes.length;
        if (inputLength % 4 != 0) {
            throw new DecodingException(
                String.format(
                    "Length of Base64 encoded input must be multiple of 4: %s",
                    Arrays.toString(bytes)
                )
            );
        }
        while (inputLength > 0 && bytes[inputLength - 1] == PADDING_CHAR) {
            inputLength--;
        }
        final int resultLength = (inputLength * UNENCOD_BLOCK_LEN)
            / ENCOD_BLOCK_LEN;
        byte[] out = new byte[resultLength];
        inputPos = 0;
        int outputPos = 0;
        while (inputPos < inputLength) {
            final int firstBlock =
                this.calcDecBlock(1, bytes, inputLength);
            final int secondBlock =
                this.calcDecBlock(2, bytes, inputLength);
            final int thirdBlock =
                this.calcDecBlock(3, bytes, inputLength);
            final int forthBlock =
                this.calcDecBlock(4, bytes, inputLength);
            out[outputPos++] = (byte) ((firstBlock << 2) | (secondBlock >>> 4));
            if (outputPos < resultLength) {
                out[outputPos++] = (byte) (((secondBlock & 0xf) << 4)
                    | (thirdBlock >>> 2));
            }
            if (outputPos < resultLength) {
                out[outputPos++] =
                    (byte) (((thirdBlock & 3) << 6) | forthBlock);
            }
        }
        return this.origin.decode(out);
    }
    /**
     * Extract each of 3 octets from encoding input byte array
     * and returns its value..
     * @param octetNumber Octet sequence number
     * @param inputArray Encoding byte array
     * @return Integer octet value
     */
    private int calcEncOctet(final int octetNumber, final byte[] inputArray) {
        int result;
        switch (octetNumber) {
            case 1 :
                result = inputArray[inputPos++] & MASK_8BITS;
                break;
            case 2 :
            case 3 :
                result = inputPos < inputArray.length
                    ? inputArray[inputPos++] & MASK_8BITS : 0;
                break;
            default:
                throw new IllegalStateException("Impossible octet: "
                    .concat(Integer.toString(octetNumber)));
        }
        return result;
    }

    /**
     * Extracts each of 4 blocks from decoding input byte array
     * and returns its value.
     * @param blockNumber Block sequence number
     * @param inputArray Decoding byte array
     * @param inputLength Decoding byte array length
     * @return Integer block value
     */
    private int calcDecBlock(final int blockNumber, final byte[] inputArray,
                             final int inputLength) {
        int result;
        switch (blockNumber) {
            case 1 :
            case 2:
                result = DECODE_TABLE[inputArray[inputPos++]];
                break;
            case 3 :
            case 4 :
                result = DECODE_TABLE[inputPos < inputLength
                    ? inputArray[inputPos++] : 'A'];
                break;
            default:
                throw new IllegalStateException("Impossible block"
                    .concat(Integer.toString(blockNumber)));
        }
        if (result < 0) {
            throw new DecodingException(
                String.format(
                    "Illegal character in Base64 encoded data. %s",
                    Arrays.toString(inputArray)
                )
            );
        }
        return result;
    }
}
