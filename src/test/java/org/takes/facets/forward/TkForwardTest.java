/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.forward;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.StartsWith;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rs.ResponseOf;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkForward}.
 * @since 0.2
 * @checkstyle ClassDataAbstractionCouplingCheck (100 lines)
 */
final class TkForwardTest {

    @Test
    void catchesExceptionCorrectly() throws Exception {
        final Take take = request -> {
            throw new RsForward("/");
        };
        MatcherAssert.assertThat(
            "TkForward must catch RsForward exception and return redirect response",
            new RsPrint(
                new TkForward(take).act(new RqFake())
            ),
            new StartsWith("HTTP/1.1 303 See Other")
        );
    }

    @Test
    void catchesExceptionThrownByResponse() throws Exception {
        final Take take =
            request -> new ResponseOf(
                () -> new RsEmpty().head(),
                () -> {
                    throw new RsForward("/b");
                }
            );
        MatcherAssert.assertThat(
            "TkForward must catch RsForward exception thrown by response body and return redirect",
            new RsPrint(
                new TkForward(take).act(new RqFake())
            ),
            new StartsWith("HTTP/1.1 303")
        );
    }

}
