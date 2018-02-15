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
package org.takes.facets.fork;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.takes.Request;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link FkHitRefresh}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.9
 */
public final class FkHitRefreshTest {

    /**
     * Temp directory.
     */
    @Rule
    public final transient TemporaryFolder temp = new TemporaryFolder();

    /**
     * FkHitRefresh can refresh on demand.
     * @throws Exception If some problem inside
     */
    @Test
    public void refreshesOnDemand() throws Exception {
        final Request req = new RqWithHeader(
            new RqFake(), "X-Takes-HitRefresh: yes"
        );
        final AtomicBoolean done = new AtomicBoolean(false);
        final Fork fork = new FkHitRefresh(
            this.temp.getRoot(),
            new Runnable() {
                @Override
                public void run() {
                    done.set(true);
                }
            },
            new TkEmpty()
        );
        TimeUnit.SECONDS.sleep(2L);
        FileUtils.touch(this.temp.newFile("hey.txt"));
        MatcherAssert.assertThat(
            fork.route(req).has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(done.get(), Matchers.is(true));
    }

    /**
     * FkHitRefresh can ignore when header is absent.
     * @throws Exception If some problem inside
     */
    @Test
    public void ignoresWhenNoHeader() throws Exception {
        final File dir = this.temp.getRoot();
        MatcherAssert.assertThat(
            new FkHitRefresh(
                dir, "", new TkEmpty()
            ).route(new RqFake()).has(),
            Matchers.is(false)
        );
    }

}
