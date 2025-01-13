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
package org.takes.tk;

import java.net.HttpURLConnection;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsText;
import org.takes.facets.hamcrest.HmHeader;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkRedirect}.
 * @since 0.10
 */
final class TkRedirectTest {
    /**
     * Constant variable for HTTP header testing.
     */
    private static final String LOCATION = "Location: %1$s";

    /**
     * New line constant.
     */
    private static final String NEWLINE = "\r\n";

    @Test
    void createsRedirectResponseWithUrl() throws Exception {
        final String url = "/about";
        MatcherAssert.assertThat(
            new RsPrint(
                new TkRedirect(url).act(new RqFake())
            ),
            new IsText(
                new Joined(
                    TkRedirectTest.NEWLINE,
                    "HTTP/1.1 303 See Other",
                    String.format(TkRedirectTest.LOCATION, url),
                    "",
                    ""
                )
            )
        );
    }

    @Test
    void createsRedirectResponseWithUrlAndStatus() throws Exception {
        final String url = "/";
        MatcherAssert.assertThat(
            new RsPrint(
                new TkRedirect(url, HttpURLConnection.HTTP_MOVED_TEMP).act(
                    new RqFake()
                )
            ),
            new IsText(
                new Joined(
                    TkRedirectTest.NEWLINE,
                    "HTTP/1.1 302 Found",
                    String.format(TkRedirectTest.LOCATION, url),
                    "",
                    ""
                )
            )
        );
    }

    @Test
    void ignoresQueryAndFragmentOnEmptyUrl() throws Exception {
        final String target = "/the-target";
        MatcherAssert.assertThat(
            new TkRedirect(target).act(
                new RqFake("GET", "/hey-you?f=1#xxx")
            ),
            new HmHeader<>(
                "Location",
                target
            )
        );
    }

}
