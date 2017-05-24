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

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link Base64}.
 * @author Sven Windisch (sven.windisch@gmail.com)
 * @version $Id$
 * @since 1.1
 */
@Ignore
public final class Base64Test {

    /**
     * A short text to encode with Base64.
     */
    private static final String SHORTTEXT =
        "We didn't start the fire!";
    /**
     * The corresponding short Base64 key.
     */
    private static final String SHORTKEY =
        "V2UgZGlkbid0IHN0YXJ0IHRoZSBmaXJlIQ==";
    /**
     * A long text that ensures a linebreak in the key.
     * @checkstyle LineLength (3 lines)
     */
    private static final String LONGTEXT =
        "We didn't start the fire, it was always burning, since the world's been turning.";
    /**
     * The corresponding key.
     * @checkstyle LineLength (4 lines)
     * @checkstyle StringLiteralsConcatenation (3 lines)
     */
    private static final String LONGKEY =
        "V2UgZGlkbid0IHN0YXJ0IHRoZSBmaXJlLCBpdCB3YXMgYWx3YXlzIGJ1cm5pbmcsIHNpbmNlIHR" + System.lineSeparator() + "oZSB3b3JsZCdzIGJlZW4gdHVybmluZy4=";

    /**
     * Base64 can encode a String.
     * @throws IOException If some problem inside
     */
    @Test
    public void encodeThreeString() throws IOException {
        MatcherAssert.assertThat(
            new String(
                new Base64().encode("Pia")
            ),
            Matchers.equalTo("UGlh")
        );
    }

    /**
     * Base64 can encode a String.
     * @throws IOException If some problem inside
     */
    @Test
    public void encodeFourString() throws IOException {
        MatcherAssert.assertThat(
            new String(
                new Base64().encode("Pian")
            ),
            Matchers.equalTo("UGlhbg==")
        );
    }

    /**
     * Base64 can encode a String.
     * @throws IOException If some problem inside
     */
    @Test
    public void encodeFiveString() throws IOException {
        MatcherAssert.assertThat(
            new String(
                new Base64().encode("Piano")
            ),
            Matchers.equalTo("UGlhbm8=")
        );
    }

    /**
     * Base64 can encode a String.
     * @throws IOException If some problem inside
     */
    @Test
    public void encodeString() throws IOException {
        MatcherAssert.assertThat(
            new String(
                new Base64().encode(Base64Test.SHORTTEXT)
            ),
            Matchers.equalTo(Base64Test.SHORTKEY)
        );
    }

    /**
     * Base64 can encode a byte[].
     * @throws IOException If some problem inside
     */
    @Test
    public void encodeByteArray() throws IOException {
        MatcherAssert.assertThat(
            new String(
                new Base64().encode(Base64Test.SHORTTEXT.getBytes())
            ),
            Matchers.equalTo(Base64Test.SHORTKEY)
        );
    }

    /**
     * Base64 can decode a String.
     * @throws IOException If some problem inside
     */
    @Test
    public void decodeString() throws IOException {
        MatcherAssert.assertThat(
            new String(
                new Base64().decode(Base64Test.SHORTKEY)
            ),
            Matchers.equalTo(Base64Test.SHORTTEXT)
        );
    }

    /**
     * Base64 can decode a byte[].
     * @throws IOException If some problem inside
     */
    @Test
    public void decodeByteArray() throws IOException {
        MatcherAssert.assertThat(
            new String(
                new Base64()
                    .decode(Base64Test.SHORTKEY.getBytes())
            ),
            Matchers.equalTo(Base64Test.SHORTTEXT)
        );
    }

    /**
     * Base64 can encode and insert line breaks.
     * @throws IOException If some problem inside
     */
    @Test
    public void encodeWithLineBreaks() throws IOException {
        MatcherAssert.assertThat(
            new String(
                new Base64()
                    .encode(Base64Test.LONGTEXT, true)
            ),
            Matchers.equalTo(Base64Test.LONGKEY)
        );
    }

    /**
     * Base64 can decode a String with line breaks.
     * @throws IOException If some problem inside
     */
    @Test
    public void decodeWithLineBreaks() throws IOException {
        MatcherAssert.assertThat(
            new String(
                new Base64().decode(Base64Test.LONGKEY)
            ),
            Matchers.equalTo(Base64Test.LONGTEXT)
        );
    }
}
