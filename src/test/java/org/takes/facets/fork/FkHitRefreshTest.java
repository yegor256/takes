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
package org.takes.facets.fork;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.takes.Request;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link FkHitRefresh}.
 * @since 0.9
 */
final class FkHitRefreshTest {

    @Test
    void refreshesOnDemand(@TempDir final Path temp) throws Exception {
        final Request req = new RqWithHeader(
            new RqFake(), "X-Takes-HitRefresh: yes"
        );
        final AtomicBoolean done = new AtomicBoolean(false);
        final Fork fork = new FkHitRefresh(
            temp.toFile(),
            () -> done.set(true),
            new TkEmpty()
        );
        TimeUnit.MILLISECONDS.sleep(10L);
        FileUtils.touch(temp.resolve("hey.txt").toFile());
        MatcherAssert.assertThat(
            fork.route(req).has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(done.get(), Matchers.is(true));
    }

    @Test
    void ignoresWhenNoHeader(@TempDir final File temp) throws Exception {
        MatcherAssert.assertThat(
            new FkHitRefresh(
                temp, "", new TkEmpty()
            ).route(new RqFake()).has(),
            Matchers.is(false)
        );
    }

}
