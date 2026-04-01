/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MediaType}.
 * @since 0.6
 */
final class MediaTypeTest {

    @Test
    void wildcardMatchesSpecific() {
        MatcherAssert.assertThat(
            "Wildcard media type must match any specific type",
            new MediaType("*/*").matches(new MediaType("application/pdf")),
            Matchers.is(true)
        );
    }

    @Test
    void specificMatchesWildcard() {
        MatcherAssert.assertThat(
            "Specific media type must match wildcard",
            new MediaType("application/xml").matches(new MediaType("*/* ")),
            Matchers.is(true)
        );
    }

    @Test
    void specificSubtypeMatchesWildcard() {
        MatcherAssert.assertThat(
            "Specific subtype must match wildcard subtype",
            new MediaType("text/html").matches(new MediaType("text/*")),
            Matchers.is(true)
        );
    }

    @Test
    void wildcardSubtypeMatchesSpecific() {
        MatcherAssert.assertThat(
            "Wildcard subtype must match specific subtype",
            new MediaType("image/*").matches(new MediaType("image/png")),
            Matchers.is(true)
        );
    }

    @Test
    void differentTypesNotMatch() {
        MatcherAssert.assertThat(
            "Different media types must not match",
            new MediaType("application/json").matches(new MediaType("text")),
            Matchers.is(false)
        );
    }

    @Test
    void comparesTwoTypes() {
        MatcherAssert.assertThat(
            "Different media types must have non-zero comparison",
            new MediaType("text/b").compareTo(new MediaType("text/a")),
            Matchers.not(Matchers.equalTo(0))
        );
    }

    @Test
    void parsesInvalidTypesWithoutException() {
        Assertions.assertDoesNotThrow(
            () -> new MediaType("hello, how are you?")
        );
    }

    @Test
    void parsesSlashesWithoutException() {
        Assertions.assertDoesNotThrow(
            () -> new MediaType("////")
        );
    }

    @Test
    void parsesQualityValueWithoutException() {
        Assertions.assertDoesNotThrow(
            () -> new MediaType("/;/;q=0.9")
        );
    }

    @Test
    void parsesWhitespaceAndSpecialCharsWithoutException() {
        Assertions.assertDoesNotThrow(
            () -> new MediaType("\n\n\t\r\u20ac00")
        );
    }

}
