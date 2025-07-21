/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.HttpException;
import org.takes.rq.RqEmpty;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsEmpty;
import org.takes.tk.TkEmpty;
import org.takes.tk.TkText;

/**
 * Test case for {@link FkHost}.
 * @since 0.32
 */
final class FkHostTest {

    @Test
    void matchesByHost() throws Exception {
        MatcherAssert.assertThat(
            new FkHost("www.foo.com", new TkText("boom"))
                .route(
                    new RqWithHeader(
                        new RqEmpty(),
                        "Host: www.foo.com"
                    )
                )
                .has(),
            Matchers.is(true)
        );
    }

    @Test
    void doesntMatchByHost() throws Exception {
        final AtomicBoolean acted = new AtomicBoolean();
        MatcherAssert.assertThat(
            new FkHost(
                "google.com",
                req -> {
                    acted.set(true);
                    return new RsEmpty();
                }
            ).route(new RqFake("PUT", "/?test")).has(),
            Matchers.is(false)
        );
        MatcherAssert.assertThat(
            "Take must not be executed when host does not match",
            acted.get(),
            Matchers.is(false)
        );
    }

    @Test
    void doesntMatchWithNoHost() {
        Assertions.assertThrows(
            HttpException.class,
            () -> new FkHost("google.com", new TkEmpty())
                .route(new RqFake(Arrays.asList("GET / HTTP/1.1"), "body"))
        );
    }

}
