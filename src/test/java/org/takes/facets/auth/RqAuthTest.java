/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;

/**
 * Test case for {@link RqAuth}.
 * @since 0.9.12
 */
final class RqAuthTest {

    @Test
    void returnsIdentity() throws IOException {
        final Identity.Simple identity = new Identity.Simple("urn:test:1");
        MatcherAssert.assertThat(
            "RqAuth must decode identity from auth header",
            new RqAuth(
                new RqWithHeader(
                    new RqFake(),
                    TkAuth.class.getSimpleName(),
                    new String(new CcPlain().encode(identity))
                )
            ).identity().urn(),
            Matchers.equalTo(identity.urn())
        );
    }
}
