/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 Yegor Bugayenko
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

/**
 * Encoding and decoding of Base64.
 *
 * @author Sven Windisch (sven.windisch@gmail.com)
 * @version $Id$
 * @since 1.1
 */
public final class Base64 {

	private final static String base64chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	/**
	 * Base64-encode the input string.
	 * This is the same as {@code encode(input, false)}.
	 * 
	 * @param input The value to be encoded.
	 * @return Encoded
	 * @throws IOException
	 */
	public byte[] encode(String input) throws IOException {
		return encode(input.getBytes(), false);
	}

	/**
	 * Base64-encode the input string.
	 * 
	 * @param input The value to be encoded.
	 * @param linebreak Set to true to insert line breaks after 76 chars.
	 * @return Encoded
	 * @throws IOException
	 */
	public byte[] encode(String input, boolean linebreak) throws IOException {
		return encode(input.getBytes(), linebreak);
	}

	/**
	 * Base64-encode the input array.
	 * This is the same as {@code encode(input, false)}.
	 * 
	 * @param input The value to be encoded.
	 * @return Encoded
	 * @throws IOException
	 */
	public byte[] encode(byte[] input) throws IOException {
		return encode(input, false);
	}
	
	/**
	 * Base64-encode the input array.
	 * 
	 * @param input The value to be encoded.
	 * @param linebreak Set to true to insert line breaks after 76 chars.
	 * @return Encoded
	 * @throws IOException
	 */
	public byte[] encode(byte[] input, boolean linebreak) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final StringBuilder padding = new StringBuilder();

		int c = input.length % 3;

		if (c > 0) {
			for (; c < 3; c++) {
				padding.append("=");
			}
		}

		final ByteBuffer bb = ByteBuffer.allocate(input.length + padding.length());
		bb.put(input);

		final byte[] bytes = bb.array();

		for (c = 0; c < bytes.length; c += 3) {

			if (linebreak && c > 0 && (c / 3 * 4) % 76 == 0) {
				out.write(System.lineSeparator().getBytes());
			}

			int n = (bytes[c] << 16) + (bytes[c + 1] << 8) + (bytes[c + 2]);
			int n1 = (n >> 18) & 63, n2 = (n >> 12) & 63, n3 = (n >> 6) & 63, n4 = n & 63;

			out.write(base64chars.charAt(n1));
			out.write(base64chars.charAt(n2));
			out.write(base64chars.charAt(n3));
			out.write(base64chars.charAt(n4));
		}
		String encoded = out.toString();

		return (encoded.substring(0, encoded.length() - padding.length()) + padding).getBytes();
	}

	/**
	 * Base64-decode the input string.
	 * Prior to decoding, all non-Base64 characters are removed.
	 * 
	 * @param input The value to be decoded.
	 * @return Decoded
	 */
	public byte[] decode(String input) {
		return decode(input.getBytes());
	}

	/**
	 * Base64-decode the input array.
	 * Prior to decoding, all non-Base64 characters are removed.
	 * 
	 * @param input The value to be decoded.
	 * @return Decoded
	 */
	public byte[] decode(byte[] input) {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final ByteBuffer bb = ByteBuffer.allocate(input.length);
		int padding = 0;

		if (input[input.length - 1] == '=') {
			input[input.length - 1] = 'A';
			padding++;
		}
		if (input[input.length - 2] == '=') {
			input[input.length - 2] = 'A';
			padding++;
		}

		for (int c = 0; c < input.length; c++) {
			if (base64chars.indexOf(input[c]) >= 0) {
				bb.put(input[c]);
			}
		}

		final byte[] bytes = bb.array();

		for (int c = 0; c < bytes.length; c += 4) {

			int n = (base64chars.indexOf(bytes[c]) << 18) + (base64chars.indexOf(bytes[c + 1]) << 12)
					+ (base64chars.indexOf(bytes[c + 2]) << 6) + base64chars.indexOf(bytes[c + 3]);

			out.write((char) ((n >>> 16) & 0xFF));
			out.write((char) ((n >>> 8) & 0xFF));
			out.write((char) ((n) & 0xFF));
		}

		String decoded = out.toString();

		return decoded.substring(0, decoded.length() - padding).getBytes();
	}
}
