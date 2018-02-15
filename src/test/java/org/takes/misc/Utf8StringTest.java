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

import java.io.UnsupportedEncodingException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Utf8String}.
 * @author Maksimenko Vladimir (xupypr@xupypr.com)
 * @version $Id$
 * @since 0.33
 */
public final class Utf8StringTest {

    /**
     * UTF-8 encoding.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * Utf8String can be constructed from string.
     * @throws UnsupportedEncodingException If fails
     */
    @Test
    public void canConstructFromString() throws UnsupportedEncodingException {
        final String test = "Hello こんにちは Привет";
        final Utf8String str = new Utf8String(test);
        MatcherAssert.assertThat(str.string(), Matchers.equalTo(test));
        MatcherAssert.assertThat(
            str.bytes(),
            Matchers.equalTo(test.getBytes(Utf8StringTest.ENCODING))
        );
    }

    /**
     * Utf8String can be constructed from bytes array.
     * @throws UnsupportedEncodingException If fails
     */
    @Test
    public void canBeConstructedFromBytes()
        throws UnsupportedEncodingException {
        final String test = "Bye 同時に Пока";
        final Utf8String str = new Utf8String(
            test.getBytes(Utf8StringTest.ENCODING)
        );
        MatcherAssert.assertThat(str.string(), Matchers.equalTo(test));
        MatcherAssert.assertThat(
            str.bytes(),
            Matchers.equalTo(test.getBytes(Utf8StringTest.ENCODING))
        );
    }
}
