/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link FkHitRefresh}.
 * @since 0.9
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class FkHitRefreshTest {

    @Test
    void matchesRequestWithHeader(@TempDir final Path temp) throws Exception {
        MatcherAssert.assertThat(
            "FkHitRefresh must match request when hit refresh header is present",
            FkHitRefreshTest.refreshFork(temp, new AtomicBoolean()).route(
                new RqWithHeader(new RqFake(), "X-Takes-HitRefresh: yes")
            ).has(),
            Matchers.is(true)
        );
    }

    @Test
    void executesTaskOnRefresh(@TempDir final Path temp) throws Exception {
        final AtomicBoolean done = new AtomicBoolean(false);
        FkHitRefreshTest.refreshFork(temp, done).route(
            new RqWithHeader(new RqFake(), "X-Takes-HitRefresh: yes")
        );
        MatcherAssert.assertThat(
            "Hit refresh task must have been executed",
            done.get(),
            Matchers.is(true)
        );
    }

    @Test
    void ignoresWhenNoHeader(@TempDir final File temp) throws Exception {
        MatcherAssert.assertThat(
            "FkHitRefresh must not match request when hit refresh header is missing",
            new FkHitRefresh(
                temp, "", new TkEmpty()
            ).route(new RqFake()).has(),
            Matchers.is(false)
        );
    }

    private static Fork refreshFork(
        final Path temp,
        final AtomicBoolean done
    ) throws Exception {
        final Fork fork = new FkHitRefresh(
            temp.toFile(),
            () -> done.set(true),
            new TkEmpty()
        );
        TimeUnit.MILLISECONDS.sleep(10L);
        FileUtils.touch(temp.resolve("hey.txt").toFile());
        return fork;
    }

}
