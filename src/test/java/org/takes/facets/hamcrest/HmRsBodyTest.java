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
import org.takes.rs.RsWithHeader;
import org.takes.rs.RsWithType;

/**
 * Test case for {@link HmRsBody}.
 * @author Eugene Kondrashev (eugene.kondrashev@gmail.com)
 * @author Andrey Eliseev (aeg.exper0@gmail.com)
 * @version $Id: bc5f637cdf2ae290ae2fe4dc3e7755c55ad4b166 $
 * @since 0.23.3
 */
public final class HmRsBodyTest {

    /**
     * HmRsBody(String)
     * @throws Exception If some problem inside
     */
    @Test
    public void testString() throws Exception {
        String body = "<html>Hello</html>";
		MatcherAssert.assertThat(
            new RsWithBody(body),
            new HmRsBody(body)
        );
    }

    /**
     * HmRsBody(String, Charset)
     * @throws Exception If some problem inside
     */
    @Test
    public void testStringCharset() throws Exception {
        String body = "<html>Hello</html>";
    	Charset charset=Charset.defaultCharset();
        MatcherAssert.assertThat(
            new RsWithType(new RsWithBody(body), "text/html;charset="+charset.name()),
            new HmRsBody(body, charset)
        );
    }

    /**
     * HmRsBody(String, Charset)
     * @throws Exception If some problem inside
     */
    @Test
    public void testStringCharsetName() throws Exception {
        String body = "<html>Hello</html>";
    	String charsetName=Charset.defaultCharset().name();
        MatcherAssert.assertThat(
            new RsWithType(new RsWithBody(body), "text/html;charset="+charsetName),
            new HmRsBody(body, charsetName)
        );
    }

    /**
     * HmRsBody(byte[])
     * @throws Exception If some problem inside
     */
    @Test
    public void testByteArray() throws Exception {
        byte[] body = "<html>Hello</html>".getBytes();
		MatcherAssert.assertThat(
            new RsWithBody(body),
            new HmRsBody(body)
        );
    }

}
