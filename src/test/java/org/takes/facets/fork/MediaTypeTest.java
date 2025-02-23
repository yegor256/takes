/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MediaType}.
 * @since 0.6
 */
final class MediaTypeTest {

    @Test
    void matchesTwoTypes() {
        MatcherAssert.assertThat(
            new MediaType("*/*").matches(new MediaType("application/pdf")),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new MediaType("application/xml").matches(new MediaType("*/* ")),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new MediaType("text/html").matches(new MediaType("text/*")),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new MediaType("image/*").matches(new MediaType("image/png")),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new MediaType("application/json").matches(
                new MediaType("text")
            ),
            Matchers.is(false)
        );
    }

    @Test
    void comparesTwoTypes() {
        MatcherAssert.assertThat(
            new MediaType("text/b").compareTo(new MediaType("text/a")),
            Matchers.not(Matchers.equalTo(0))
        );
    }

    @Test
    void parsesInvalidTypes() {
        new MediaType("hello, how are you?");
        new MediaType("////");
        new MediaType("/;/;q=0.9");
        new MediaType("\n\n\t\r\u20ac00");
    }

}
