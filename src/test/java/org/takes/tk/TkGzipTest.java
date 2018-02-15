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
package org.takes.tk;

import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkGzip}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.17
 */
public final class TkGzipTest {

    /**
     * TkGzip can compress on demand only.
     * @throws IOException If some problem inside
     */
    @Test
    public void compressesOnDemandOnly() throws IOException {
        MatcherAssert.assertThat(
            new RsPrint(
                new TkGzip(new TkClasspath()).act(
                    new RqFake(
                        Arrays.asList(
                            "GET /org/takes/tk/TkGzip.class HTTP/1.1",
                            "Host: www.example.com",
                            "Accept-Encoding: gzip"
                        ),
                        ""
                    )
                )
            ).print(),
            Matchers.startsWith("HTTP/1.1 200 OK")
        );
    }

    /**
     * TkGzip can return uncompressed content.
     * @throws IOException If some problem inside
     */
    @Test
    public void doesntCompressIfNotRequired() throws IOException {
        MatcherAssert.assertThat(
            new RsPrint(
                new TkGzip(new TkClasspath()).act(
                    new RqFake(
                        Arrays.asList(
                            "GET /org/takes/tk/TkGzip.class HTTP/1.0",
                            "Host: abc.example.com"
                        ),
                        ""
                    )
                )
            ).print(),
            Matchers.startsWith("HTTP/1.1 200")
        );
    }

}
