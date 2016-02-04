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
package org.takes.facets.ret;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link RsReturn}.
 * @author Ivan Inozemtsev (ivan.inozemtsev@gmail.com)
 * @version $Id$
 * @since 0.20
 */
public final class RsReturnTest {

    /**
     * RsReturn can add cookies.
     * @throws IOException If some problem inside
     */
    @Test
    public void addsCookieToResponse() throws IOException {
        final String destination = "/return/to";
        MatcherAssert.assertThat(
            new RsPrint(
                new RsReturn(new RsEmpty(), destination)
            ).print(),
            Matchers.containsString(
                String.format(
                    "Set-Cookie: RsReturn=%s;Path=/",
                    URLEncoder.encode(
                        destination,
                        Charset.defaultCharset().name()
                    )
                )
            )
        );
    }

    /**
     * RsReturn can create cookie with expires attribute in GMT..
     * @throws IOException If there are some problems inside
     */
    @Test
    public void createsCookieWithExpiresInGMT() throws IOException {
        final SimpleDateFormat format = new SimpleDateFormat(
            "'Expires='EEE, dd MMM yyyy HH:mm:ss z;",
            Locale.ENGLISH
        );
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        MatcherAssert.assertThat(
            new RsPrint(
                new RsReturn(new RsEmpty(), "/return/where")
            ).print(),
            Matchers.containsString(
                format.format(
                    new Date(System.currentTimeMillis()
                        + TimeUnit.HOURS.toMillis(1L)
                    )
                )
            )
        );
    }
}
