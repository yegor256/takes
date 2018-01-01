/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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

package org.takes.misc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Encoding and decoding of Base64.
 *
 * @author Sven Windisch (sven.windisch@gmail.com)
 * @version $Id$
 * @since 1.1
 */
public final class Base64 {

    /**
     * All legal Base64 chars.
     */
    private static final String BASECHARS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    /**
     * The charset.
     */
    private final Charset charset;

    /**
     * Ctor.
     */
    public Base64() {
        this.charset = Charset.defaultCharset();
    }

    /**
     * Base64-encode the input string.
     * This is the same as {@code encode(input, false)}.
     *
     * @param input The value to be encoded.
     * @return Encoded
     * @throws IOException For anything gone wrong
     */
    public byte[] encode(final String input) throws IOException {
        return this.encode(input.getBytes(this.charset), false);
    }

    /**
     * Base64-encode the input string.
     *
     * @param input The value to be encoded.
     * @param linebreak Set to true to insert line breaks after 76 chars.
     * @return Encoded
     * @throws IOException For anything gone wrong
     */
    public byte[] encode(final String input, final boolean linebreak)
        throws IOException {
        return this.encode(input.getBytes(this.charset), linebreak);
    }

    /**
     * Base64-encode the input array.
     * This is the same as {@code encode(input, false)}.
     *
     * @param input The value to be encoded.
     * @return Encoded
     * @throws IOException For anything gone wrong
     */
    public byte[] encode(final byte[] input) throws IOException {
        return this.encode(input, false);
    }

    /**
     * Base64-encode the input array.
     *
     * @param input The value to be encoded.
     * @param linebreak Set to true to insert line breaks after 76 chars.
     * @return Encoded
     * @throws IOException For anything gone wrong
     */
    public byte[] encode(final byte[] input, final boolean linebreak)
        throws IOException {
        return this.toBase(input, linebreak);
    }

    /**
     * Base64-decode the input string.
     * Prior to decoding, all non-Base64 characters are removed.
     *
     * @param input The value to be decoded.
     * @return Decoded
     * @throws IOException For anything gone wrong
     */
    public byte[] decode(final String input) throws IOException {
        return this.decode(input.getBytes(this.charset));
    }

    /**
     * Base64-decode the input array.
     * Prior to decoding, all non-Base64 characters are removed.
     *
     * @param input The value to be decoded.
     * @return Decoded
     * @throws IOException For anything gone wrong
     */
    public byte[] decode(final byte[] input) throws IOException {
        return this.fromBase(input);
    }

    /**
     * Base64-encode the input data.
     *
     * @param input The value to be encoded.
     * @param linebreak Set to true to insert line breaks after 76 chars.
     * @return Encoded
     * @throws IOException For anything gone wrong
     */
    private byte[] toBase(final byte[] input, final boolean linebreak)
        throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final StringBuilder padding = new StringBuilder();
        // @checkstyle MagicNumber (3 lines)
        int pos = input.length % 3;
        if (pos > 0) {
            for (; pos < 3; ++pos) {
                padding.append('=');
            }
        }
        final ByteBuffer tmp = ByteBuffer.allocate(
            input.length + padding.length()
        );
        final byte[] bytes = tmp.put(input).array();
        // @checkstyle MagicNumber (2 lines)
        for (pos = 0; pos < bytes.length; pos += 3) {
            if (linebreak && pos > 0 && (pos / 3 * 4) % 76 == 0) {
                out.write(
                    System.lineSeparator().getBytes(this.charset)
                );
            }
            // @checkstyle MagicNumber (6 lines)
            final int inbits =
                (bytes[pos] << 16) + (bytes[pos + 1] << 8) + (bytes[pos + 2]);
            out.write(Base64.BASECHARS.charAt((inbits >> 18) & 63));
            out.write(Base64.BASECHARS.charAt((inbits >> 12) & 63));
            out.write(Base64.BASECHARS.charAt((inbits >> 6) & 63));
            out.write(Base64.BASECHARS.charAt(inbits & 63));
        }
        final String encoded = out.toString(this.charset.name());
        return (encoded.substring(
            0, encoded.length() - padding.length()
        ) + padding).getBytes(this.charset);
    }

    /**
     * Base64-decode the input array.
     * Prior to decoding, all non-Base64 characters are removed.
     *
     * @param input The value to be decoded.
     * @return Decoded
     * @throws IOException For anything gone wrong
     */
    private byte[] fromBase(final byte[] input) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ByteBuffer tmp = ByteBuffer.allocate(input.length);
        int padding = 0;
        if (input[input.length - 1] == '=') {
            input[input.length - 1] = 'A';
            ++padding;
        }
        if (input[input.length - 2] == '=') {
            input[input.length - 2] = 'A';
            ++padding;
        }
        for (int pos = 0; pos < input.length; ++pos) {
            if (Base64.BASECHARS.indexOf(input[pos]) >= 0) {
                tmp.put(input[pos]);
            }
        }
        final byte[] bytes = tmp.array();
        // @checkstyle MagicNumber (9 lines)
        for (int pos = 0; pos < bytes.length; pos += 4) {
            final int bits =
                (Base64.BASECHARS.indexOf(bytes[pos]) << 18)
                + (Base64.BASECHARS.indexOf(bytes[pos + 1]) << 12)
                + (Base64.BASECHARS.indexOf(bytes[pos + 2]) << 6)
                + Base64.BASECHARS.indexOf(bytes[pos + 3]);
            out.write((char) ((bits >>> 16) & 0xFF));
            out.write((char) ((bits >>> 8) & 0xFF));
            out.write((char) (bits & 0xFF));
        }
        final String decoded = out.toString(this.charset.name());
        return decoded.substring(
            0, decoded.length() - padding
        ).getBytes(this.charset);
    }
}
