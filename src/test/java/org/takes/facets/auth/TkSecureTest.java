/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.Take;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.facets.forward.RsForward;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsEmpty;

/**
 * Test case for {@link TkSecure}.
 * @since 0.11
 */
final class TkSecureTest {

    @Test
    void failsOnAnonymous() {
        final Take secure = new TkSecure(request -> new RsEmpty());
        final RsForward exception = Assertions.assertThrows(
            RsForward.class,
            () -> secure.act(new RqFake())
        );
        Assertions.assertEquals(
            HttpURLConnection.HTTP_UNAUTHORIZED,
            exception.code(),
            "Anonymous access must result in HTTP 401 Unauthorized"
        );
    }

    @Test
    void passesOnRegisteredUser() throws Exception {
        MatcherAssert.assertThat(
            new TkSecure(
                request -> new RsEmpty()
            ).act(
                new RqWithHeader(
                    new RqFake(),
                    TkAuth.class.getSimpleName(),
                    new String(
                        new CcPlain().encode(new Identity.Simple("urn:test:2"))
                    )
                )
            ),
            Matchers.instanceOf(RsEmpty.class)
        );
    }
}
