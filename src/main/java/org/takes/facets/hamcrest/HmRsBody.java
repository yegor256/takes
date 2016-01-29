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
import java.util.Arrays;
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
 * @version $Id$
 * @author Alexei Kaigorodov (alexei.kaigorodov@gmail.com)
 */
public final class HmRsBody extends TypeSafeMatcher<Response> {

    /**
     * Just a constant.
     */
    private static final int BUF_LEN = 2048;

    /**
     * Pattern to extract charset name.
     */
    private static final Pattern PATT = Pattern
            .compile("^\\s*charset\\s*=\\s*(\\w*)\\s*$");

    /**
     * Body value as byte array.
     */
    private transient byte[] value;

    /**
     * Body value as String.
     */
    private transient String stringvalue;

    /**
     * Encoding of the body byte array.
     */
    private transient Charset charset;

    /**
     * Makes HmRsBody from String.
     * @param str String body
     */
    public HmRsBody(final String str) {
        this(str, (Charset) null);
    }

    /**
     * Makes HmRsBody from String.
     * @param str String to compare
     * @param charsetname Charset name
     */
    public HmRsBody(final String str, final String charsetname) {
        this(str, Charset.forName(charsetname));
    }

    /**
     * Makes HmRsBody from String.
     * @param str String to compare
     * @param charsetp Charset
     */
    public HmRsBody(final String str, final Charset charsetp) {
        super();
        if (str == null) {
            throw new IllegalArgumentException("str may not be null");
        }
        this.stringvalue = str;
        if (this.charset != null) {
            this.value = str.getBytes(charsetp);
            this.charset = charsetp;
        }
    }

    /**
     * Makes HmRsBody from byte array.
     * @param val Source array
     */
    public HmRsBody(final byte[] val) {
        super();
        if (val == null) {
            throw new IllegalArgumentException("byte array may not be null");
        }
        this.value = Arrays.copyOf(val, val.length);
    }

    @Override
    public void describeTo(final Description description) {
        if (this.stringvalue == null) {
            this.stringvalue = new String(this.value, this.charset);
        }
        description.appendText("body: ").appendText(this.stringvalue);
    }

    @Override
    public void describeMismatchSafely(final Response response,
            final Description description) {
        description.appendText("header was: ").appendDescriptionOf(this);
    }

    @Override
    public boolean matchesSafely(final Response response) {
        final boolean res;
        try {
            this.extractCharsetName(response);
            final InputStream body = response.body();
            try {
                if (this.value == null) {
                    if (this.stringvalue == null) {
                        throw new IllegalStateException(
                                "both string and byte arrays are null"
                             );
                    }
                    res = this.compareStrings(body);
                } else {
                    res = this.compareByteArrays(body);
                }
            } finally {
                if (body != null) {
                    body.close();
                }
            }
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
        return res;
    }

    /**
     * Compares {@stringvalue} with given byte stream
     * taking into account charset.
     * @param body Byte stream
     * @return True if {@stringvalue} equals {@body}
     * @throws IOException When reading from {@body} fails
     */
    private boolean compareStrings(final InputStream body) throws IOException {
        final InputStreamReader reader;
        if (this.charset == null) {
            reader = new InputStreamReader(body);
        } else {
            reader = new InputStreamReader(body, this.charset);
        }
        final boolean res;
        if (this.stringvalue.length() == 0) {
            res = reader.read() == -1;
        } else {
            final int buflen = Math.min(this.stringvalue.length(), BUF_LEN);
            final char[] buf = new char[buflen];
            mainloop: for (int total = 0;;) {
                final int rrb = reader.read(buf);
                if (rrb == -1) {
                    res = total == this.stringvalue.length();
                    break mainloop;
                }
                for (int kkk = 0; kkk < rrb; kkk = kkk + 1) {
                    final int index = total + kkk;
                    if (buf[kkk] != this.stringvalue.charAt(index)) {
                        res = false;
                        break mainloop;
                    }
                }
                total += rrb;
            }
        }
        return res;
    }

    /**
     * Compares {@value} with byte stream.
     * @param body Byte stream
     * @return True if @value equals @body
     * @throws IOException when IO fails
     */
    private boolean compareByteArrays(final InputStream body)
            throws IOException {
        final boolean res;
        if (this.value.length == 0) {
            res = body.read() == -1;
        } else {
            final int buflen = Math.min(this.value.length, BUF_LEN);
            final byte[] buf = new byte[buflen];
            mainloop: for (int total = 0;;) {
                final int brd = body.read(buf);
                if (brd == -1) {
                    res = total == this.value.length;
                    break;
                }
                for (int kkk = 0; kkk < brd; kkk = kkk + 1) {
                    if (buf[kkk] != this.value[total + kkk]) {
                        res = false;
                        break mainloop;
                    }
                }
                total += brd;
            }
        }
        return res;
    }

    /**
     * Try to extract charset from header.
     * @param response Response
     * @throws IOException when IO fails
     */
    private void extractCharsetName(final Response response)
            throws IOException {
        if (this.charset != null) {
            return;
        }
        final Iterable<String> head = response.head();
        final Iterator<String> hit = head.iterator();
        while (hit.hasNext()) {
            final java.util.regex.Matcher strMatcher = PATT.matcher(hit.next());
            if (strMatcher.find()) {
                try {
                    this.charset = Charset.forName(strMatcher.group());
                } catch (final IllegalCharsetNameException exp) {
                }
                break;
            }
        }
    }
}
