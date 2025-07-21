/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.takes.facets.hamcrest.HmHeader;
import org.takes.rq.RqFake;

/**
 * Test case for {@link TkSmartRedirect}.
 * @since 0.10
 */
final class TkSmartRedirectTest {
    @Test
    void redirectCarriesQueryAndFragment() throws Exception {
        MatcherAssert.assertThat(
            "TkSmartRedirect must merge query and fragment from original request with redirect URL",
            new TkSmartRedirect("http://www.google.com/abc?b=2").act(
                new RqFake("GET", "/hi?a=1#test")
            ),
            new HmHeader<>(
                "Location",
                "http://www.google.com/abc?b=2&a=1#test"
            )
        );
    }

    @Test
    void redirectCarriesQueryAndFragmentOnEmptyUrl() throws Exception {
        MatcherAssert.assertThat(
            "TkSmartRedirect must use root path when no URL provided and preserve query and fragment",
            new TkSmartRedirect().act(
                new RqFake("GET", "/hey-you?f=1#xxx")
            ),
            new HmHeader<>(
                "Location",
                "/?f=1#xxx"
            )
        );
    }

}
