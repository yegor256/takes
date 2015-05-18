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
package org.takes.facets.fallback;

import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.misc.Condition;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;
import org.takes.tk.TkFixed;

/**
 * Test case for {@link FbStatus}.
 * @author Ivan Inozemtsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.16.10
 */
public final class FbStatusTest {

    /**
     * FbStatus should react on status equal to passed in constructor.
     * @throws IOException If some problem inside
     */
    @Test
    public void reactsExactStatus() throws IOException {
        final int status = HttpURLConnection.HTTP_NOT_FOUND;
        final RqFallback req = new RqFallback.Fake(status);
        MatcherAssert.assertThat(
            new RsPrint(
                new FbStatus(
                    status,
                    new TkFixed(new RsText("not found response"))
                ).route(req).next()
            ).printBody(),
            Matchers.startsWith("not found")
        );
    }

     /**
     * FbStatus should react on status which fits to condition passed in
     * constructor.
     * @throws IOException If some problem inside
     */
    @Test
    public void reactsFittingStatus() throws IOException {
        final RqFallback req = new RqFallback.Fake(
            HttpURLConnection.HTTP_MOVED_PERM
        );
        MatcherAssert.assertThat(
            new RsPrint(
                new FbStatus(
                    new Condition<Integer>() {
                        @Override
                        public boolean fits(final Integer status) {
                            return status == HttpURLConnection.HTTP_MOVED_PERM
                                || status == HttpURLConnection.HTTP_MOVED_TEMP;
                        };
                    },
                    new FbFixed(new RsText("response text"))
                ).route(req).next()
            ).printBody(),
            Matchers.startsWith("response")
        );
    }

    /**
     * FbStatus should react on status equal to passed in constructor.
     * @throws IOException If some problem inside
     */
    @Test
    public void ignoresOtherStatus() throws IOException {
        final RqFallback req = new RqFallback.Fake(
            HttpURLConnection.HTTP_NOT_FOUND
        );
        MatcherAssert.assertThat(
            new FbStatus(
                HttpURLConnection.HTTP_UNAUTHORIZED,
                new TkFixed(new RsText("unauthorized"))
            ).route(req).hasNext(),
            Matchers.equalTo(false)
        );
    }
}
