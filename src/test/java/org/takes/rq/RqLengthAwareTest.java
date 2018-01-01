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
package org.takes.rq;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RqLengthAware}.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class RqLengthAwareTest {

    /**
     * RqLengthAware can add length to body.
     * @throws IOException If some problem inside
     */
    @Test
    public void addsLengthToBody() throws IOException {
        MatcherAssert.assertThat(
            new RqLengthAware(
                new RqFake(
                    Arrays.asList(
                        "GET /h?a=3",
                        "Host: www.example.com",
                        "Content-type: text/plain",
                        "Content-Length: 2"
                    ),
                    "hi"
                )
            ).body().available(),
            Matchers.equalTo(2)
        );
    }

    /**
     * RqLengthAware can add BIG length to body.
     * @throws IOException If some problem inside
     */
    @Test
    public void addsBigLengthToBody() throws IOException {
        MatcherAssert.assertThat(
            new RqLengthAware(
                new RqFake(
                    Arrays.asList(
                        "GET /hi-there",
                        "Host: a.example.com",
                        "Content-type: text/xml",
                        "Content-Length: 9223372036854775000"
                    ),
                    "HI"
                )
            ).body().available(),
            Matchers.equalTo(Integer.MAX_VALUE)
        );
    }

    /**
     * RqLengthAware can read byte.
     * @throws IOException If some problem inside
     */
    @Test
    public void readsByte() throws IOException {
        final String data = "test";
        final InputStream stream = new RqLengthAware(
            new RqFake(
                Arrays.asList(
                    "GET /test1",
                    "Host: b.example.com",
                    contentLengthHeader(data.getBytes().length)
                ),
                data
            )
        ).body();
        MatcherAssert.assertThat(
            stream.read(),
            Matchers.equalTo((int) data.getBytes()[0])
        );
        MatcherAssert.assertThat(
            stream.available(),
            Matchers.equalTo(data.length() - 1)
        );
    }

    /**
     * RqLengthAware can read byte array.
     * @throws IOException If some problem inside
     */
    @Test
    public void readsByteArray() throws IOException {
        final String data = "array";
        final InputStream stream = new RqLengthAware(
            new RqFake(
                Arrays.asList(
                    "GET /test2",
                    "Host: c.example.com",
                    "Content-type: text/csv",
                    contentLengthHeader(data.getBytes().length)
                ),
                data
            )
        ).body();
        final byte[] buf = new byte[data.length()];
        MatcherAssert.assertThat(
            stream.read(buf),
            Matchers.equalTo(data.length())
        );
        MatcherAssert.assertThat(buf, Matchers.equalTo(data.getBytes()));
        MatcherAssert.assertThat(stream.available(), Matchers.equalTo(0));
    }

    /**
     * RqLengthAware can read partial array.
     * @throws IOException If some problem inside
     */
    @Test
    public void readsPartialArray() throws IOException {
        final String data = "hello world";
        final int len = 3;
        final InputStream stream = new RqLengthAware(
            new RqFake(
                Arrays.asList(
                    "GET /test3",
                    "Host: d.example.com",
                    contentLengthHeader(data.getBytes().length)
                ),
                data
            )
        ).body();
        final byte[] buf = new byte[len];
        MatcherAssert.assertThat(
            stream.read(buf, 0, len),
            Matchers.equalTo(len)
        );
        MatcherAssert.assertThat(
            buf,
            Matchers.equalTo(data.substring(0, len).getBytes())
        );
        MatcherAssert.assertThat(
            stream.available(),
            Matchers.equalTo(data.length() - len)
        );
    }

    /**
     * Format Content-Length header.
     * @param length Body length
     * @return Content-Length header
     */
    private static String contentLengthHeader(final long length) {
        return String.format("Content-Length: %d", length);
    }
}
