/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link PsCookie}.
 * @since 0.10
 */
final class PsCookieTest {

    @Test
    void addsCookieToResponse() throws IOException {
        new Assertion<>(
            "Response with Set-Cookie header",
            new RsPrint(
                new PsCookie(
                    new CcPlain(), "foo", 1L
                ).exit(new RsEmpty(), new Identity.Simple("urn:test:99"))
            ),
            new HasString(
                "Set-Cookie: foo=urn%3Atest%3A99;Path=/;HttpOnly;"
            )
        ).affirm();
    }
}
