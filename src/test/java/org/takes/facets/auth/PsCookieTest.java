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
package org.takes.facets.auth;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link PsCookie}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.10
 */
public final class PsCookieTest {

    /**
     * PsCookie can add a cookie.
     * @throws IOException If some problem inside
     */
    @Test
    public void addsCookieToResponse() throws IOException {
        MatcherAssert.assertThat(
            new RsPrint(
                new PsCookie(
                    new CcPlain(), "foo", 1L
                ).exit(new RsEmpty(), new Identity.Simple("urn:test:99"))
            ).print(),
            Matchers.containsString(
                "Set-Cookie: foo=urn%3Atest%3A99;Path=/;HttpOnly;"
            )
        );
    }

    /**
     * PsCookie can create cookie with expires attribute in GMT.
     * @throws IOException If there are some problems inside
     */
    @Test
    public void createsCookieWithExpiresInGMT() throws IOException {
        final long age = 1L;
        final SimpleDateFormat format = new SimpleDateFormat(
            "'Expires='EEE, dd MMM yyyy HH:mm:ss z;",
            Locale.ENGLISH
        );
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        MatcherAssert.assertThat(
            new RsPrint(
                new PsCookie(
                    new CcPlain(), "bar", age
                ).exit(new RsEmpty(), Identity.ANONYMOUS)
            ).print(),
            Matchers.containsString(
                format.format(
                    new Date(System.currentTimeMillis()
                        + TimeUnit.DAYS.toMillis(age)
                    )
                )
            )
        );
    }
}
