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
package org.takes.facets.hamcrest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.takes.Response;

/**
 * Response Header Matcher.
 *
 * <p>This "matcher" tests given response body.
 * <p>The class is immutable and thread-safe.
 *
 * @version
 * @author Alexei Kaigorodov (alexei.kaigorodov@gmail.com)
 */
public final class HmRsBody extends TypeSafeMatcher<Response> {

    private static final int BUF_LEN = 2048;

    /**
     * Pattern to extract charset name.
     */
    private static final Pattern PATT = Pattern
                    .compile("^\\s*charset\\s*=\\s*(\\w*)\\s*$");

    /**
     * Body value as byte array.
     */
    private byte[] value;

    /**
     * Body value as String.
     */
    private String stringvalue;

    /**
     * encoding of the body byte array.
     */
    private Charset charset;

    /**
     * Makes HmRsBody from String
     * @param str
     */
    public HmRsBody(String str) {
        this(str, (Charset) null);
    }

    /**
     * Makes HmRsBody from String
     * @param str
     * @param charsetName
     */
    public HmRsBody(String str, String charsetName) {
        this(str, Charset.forName(charsetName));
    }

    /**
     * Makes HmRsBody from String
     * @param str
     * @param charset
     */
    public HmRsBody(String str, Charset charset) {
        if (str == null) {
            throw new IllegalArgumentException("str may not be null");
        }
        this.stringvalue = str;
        if (this.charset != null) {
            value = str.getBytes(charset);
            this.charset = charset;
        }
    }

    /**
     * Makes HmRsBody from byte array.
     * @param val
     */
    public HmRsBody(final byte[] val) {
        if (val == null) {
            throw new IllegalArgumentException("byte array may not be null");
        }
        this.value = val;
    }

    @Override
    public void describeTo(final Description description) {
        if (this.stringvalue == null) {
            this.stringvalue = new String(this.value, this.charset);
        }
        description.appendText("body: ")
                        .appendText(stringvalue);
    }

    @Override
    public boolean matchesSafely(final Response response) {
        try {
            extractCharsetName(response);

            InputStream body = response.body();
            try {
                if (this.value != null) {
                    return this.compareByteArrays(body);
                } else if (this.stringvalue != null) {
                    return this.compareStrings(body);
                } else {
                    throw new IllegalStateException(
                                    "both string and byte arrays are null"
                                    );
                }
            } finally {
                if (body != null) {
                    body.close();
                }
            }
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private boolean compareStrings(InputStream body) throws IOException {
        InputStreamReader reader;
        if (this.charset != null) {
            reader = new InputStreamReader(body, this.charset);
        } else {
            reader = new InputStreamReader(body);
        }
        if (this.stringvalue.length() == 0) {
            return reader.read() == -1;
        }
        final int buflen = Math.min(this.stringvalue.length(), BUF_LEN);
        char[] buf = new char[buflen];
        for (int total = 0; ; ) {
            final int rrb = reader.read(buf);
            if (rrb == -1) {
                return total == this.stringvalue.length();
            }
            for (int kkk = 0; kkk < rrb; kkk = kkk+1) {
                int index = total + kkk;
                if (buf[kkk] != this.stringvalue.charAt(index)) {
                    return false;
                }
            }
            total += rrb;
        }
    }

    private boolean compareByteArrays(InputStream body) throws IOException {
        if (this.value.length == 0) {
            return (body.read() == -1);
        }
        int bufLen = Math.min(this.value.length, BUF_LEN);
        byte[] buf = new byte[bufLen];
        for (int total = 0;;) {
            int rd = body.read(buf);
            if (rd == -1) {
                return total == this.value.length;
            }
            for (int k = 0; k < rd; k++) {
                if (buf[k] != this.value[total + k]) {
                    return false;
                }
            }
            total += rd;
        }
    }

    /**
     * Try to extract charset from header.
     * @param response
     * @throws IOException
     */
    private void extractCharsetName(final Response response)
                    throws IOException {
        if (this.charset != null) {
            return;
        }
        Iterable<String> head = response.head();
        Iterator<String> it = head.iterator();
        while (it.hasNext()) {
            java.util.regex.Matcher strMatcher = PATT
                            .matcher(it.next());
            if (strMatcher.find()) {
                try {
                    this.charset = Charset.forName(strMatcher.group());
                } catch (IllegalCharsetNameException e) {
                }
                break;
            }
        }
    }

    @Override
    public void describeMismatchSafely(final Response response,
                    final Description description) {
        description.appendText("header was: ")
                        .appendDescriptionOf(this);
    }
}
