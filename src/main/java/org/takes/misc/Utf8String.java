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

import java.nio.charset.Charset;

/**
 * String that uses UTF-8 encoding for all byte operations.
 * @author Maksimenko Vladimir (xupypr@xupypr.com)
 * @version $Id$
 * @since 0.33
 */
public final class Utf8String {

    /**
     * UTF-8 encoding.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * String value.
     */
    private final String value;

    /**
     * Ctor.
     * @param string String value
     */
    public Utf8String(final String string) {
        this.value = string;
    }

    /**
     * Ctor.
     * @param bytes Bytes to construct UTF-8 string value
     */
    public Utf8String(final byte... bytes) {
        this(new String(bytes, Charset.forName(Utf8String.ENCODING)));
    }

    /**
     * Encodes string value into a sequence of bytes using UTF-8 charset.
     * @return Sequence of bytes
     */
    public byte[] bytes() {
        return this.value.getBytes(Charset.forName(Utf8String.ENCODING));
    }

    /**
     * Returns string value.
     * @return String value
     */
    public String string() {
        return this.value;
    }
}
