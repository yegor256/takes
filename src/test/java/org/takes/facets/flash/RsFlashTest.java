/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
import org.cactoos.text.FormattedText;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.misc.Expires;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link RsFlash}.
 * @since 0.9.6
 */
final class RsFlashTest {

    @Test
    void addsCookieToResponse() throws IOException {
        final String msg = "hey, how are you?";
        new Assertion<>(
            "Response must contain a flash cookie",
            new RsPrint(
                new RsFlash(msg)
            ),
            new HasString(
                new FormattedText(
                    "Set-Cookie: RsFlash=%s/%s",
                    URLEncoder.encode(
                        msg,
                        Charset.defaultCharset().name()
                    ),
                    Level.INFO.getName()
                )
            )
        ).affirm();
    }

    @Test
    void addsCookieWithSpecifiedExpiresToResponse() throws IOException {
        new Assertion<>(
            "Response must contain a flash cookie with an expiration date.",
            new RsPrint(
                new RsFlash("i'm good, thanks", new Expires.Date(0L))
            ),
            new HasString("Expires=Thu, 01 Jan 1970 00:00:00 GMT")
        ).affirm();
    }

    @Test
    void printsItselfFromThrowable() {
        new Assertion<>(
            "RsFlash should print a message from Throwable",
            () -> new RsFlash(
                new IOException("and you?")
            ).toString(),
            new HasString("text=SEVERE/and you?")
        ).affirm();
    }
}
