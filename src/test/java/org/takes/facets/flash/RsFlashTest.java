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
package org.takes.facets.flash;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.logging.Level;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.misc.ExpirationDate;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link RsFlash}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.9.6
 */
public final class RsFlashTest {

    /**
     * RsFlash can add cookies.
     * @throws IOException If some problem inside
     */
    @Test
    public void addsCookieToResponse() throws IOException {
        final String msg = "hey, how are you?";
        MatcherAssert.assertThat(
            new RsPrint(
                new RsFlash(msg)
            ).print(),
            Matchers.containsString(
                String.format(
                    "Set-Cookie: RsFlash=%s/%s",
                    URLEncoder.encode(
                        msg,
                        Charset.defaultCharset().name()
                    ),
                    Level.INFO.getName()
                )
            )
        );
    }

    /**
     * RsFlash can add cookie with specified ExpirationDate.
     * @throws IOException If some problem inside
     */
    @Test
    public void addsCookieWithSpecifiedExpiresToResponse() throws IOException {
        MatcherAssert.assertThat(
            new RsPrint(
                new RsFlash("i'm good, thanks", new ExpirationDate(0L))
            ).print(),
            Matchers.containsString(
                "Expires=Thu, 01 Jan 1970 00:00:00 GMT"
            )
        );
    }

    /**
     * RsFlash can print itself from Throwable.
     * @throws IOException If some problem inside
     */
    @Test
    public void printsItselfFromThrowable() throws IOException {
        MatcherAssert.assertThat(
            new RsFlash(
                new IOException("and you?")
            ).toString(),
            Matchers.containsString(
                "text=SEVERE/and you?"
            )
        );
    }
}
