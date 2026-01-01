/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MediaTypes}.
 * @since 0.6
 */
final class MediaTypesTest {

    @Test
    void matchesTwoTypes() {
        MatcherAssert.assertThat(
            "Wildcard media type must contain specific XML type",
            new MediaTypes("*/*").contains(
                new MediaTypes("application/xml")
            ),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "Specific PDF type must contain application wildcard type",
            new MediaTypes("application/pdf").contains(
                new MediaTypes("application/*")
            ),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "Media types with quality values must contain plain text type",
            new MediaTypes("text/html;q=0.2,*/*").contains(
                new MediaTypes("text/plain")
            ),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "Text media types must not contain invalid partial text type",
            new MediaTypes("text/html;q=1.0,text/json").contains(
                new MediaTypes("text/p")
            ),
            Matchers.is(false)
        );
        MatcherAssert.assertThat(
            "Text wildcard type must not contain application type",
            new MediaTypes("text/*;q=1.0").contains(
                new MediaTypes("application/x-file")
            ),
            Matchers.is(false)
        );
    }

    @Test
    void matchesTwoCompositeTypes() {
        MatcherAssert.assertThat(
            "Composite media types must contain matching JSON type",
            new MediaTypes("text/xml,text/json").contains(
                new MediaTypes("text/json")
            ),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "Single JSON type must contain composite types including same JSON type",
            new MediaTypes("text/x-json").contains(
                new MediaTypes("text/plain,text/x-json")
            ),
            Matchers.is(true)
        );
    }

    @Test
    void parsesInvalidTypes() {
        new MediaTypes("hello, how are you?");
        new MediaTypes("////");
        new MediaTypes("/;/;q=0.9");
        new MediaTypes(",,,a;,;a,a90.0;,.0.0,;9a0");
        new MediaTypes("\n\n\t\r\u20ac00");
    }

}
