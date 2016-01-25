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

import java.nio.charset.Charset;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithType;

/**
 * Test case for {@link HmRsBody}.
 * @author Alexei Kaigorodov (alexei.kaigorodov@gmail.com)
 * @version $Id: bc5f637cdf2ae290ae2fe4dc3e7755c55ad4b166 $
 * @since 0.23.3
 */
public final class HmRsBodyTest {

    /**
     * Valid response body.
     */
    private static final String HTML_HELLO = "<html>Hello</html>";

    /**
     * Test ctor HmRsBody(String).
     * @throws Exception If some problem inside
     */
    @Test
    public void testString() throws Exception {
        final String body = HTML_HELLO;
        MatcherAssert.assertThat(
            new RsWithBody(body),
            new HmRsBody(body)
        );
    }

    /**
     * Test ctor HmRsBody(String, Charset).
     * @throws Exception If some problem inside
     */
    @Test
    public void testStringCharset() throws Exception {
        final String body = HTML_HELLO;
        final Charset charset = Charset.defaultCharset();
        final String type = this.makeType(charset.name());
        MatcherAssert.assertThat(
            new RsWithType(new RsWithBody(body), type),
            new HmRsBody(body, charset)
        );
    }

    /**
     * Test ctor HmRsBody(String, Charset).
     * @throws Exception If some problem inside
     */
    @Test
    public void testStringCharsetName() throws Exception {
        final String body = HTML_HELLO;
        final String type = this.makeType(Charset.defaultCharset().name());
        MatcherAssert.assertThat(
            new RsWithType(new RsWithBody(body), type),
            new HmRsBody(body, Charset.defaultCharset().name())
        );
    }

    /**
     * Test ctor HmRsBody(byte[]).
     * @throws Exception If some problem inside
     */
    @Test
    public void testByteArray() throws Exception {
        final byte[] body = HTML_HELLO.getBytes();
        MatcherAssert.assertThat(
            new RsWithBody(body),
            new HmRsBody(body)
        );
    }

    /**
     * makes Content-Type header string.
     * @param charsetName
     * @return header string
     */
    private String makeType(final String charsetName) {
        final StringBuilder sbd = new StringBuilder("text/html;charset=");
        sbd.append(charsetName);
        return sbd.toString();
    }

}
