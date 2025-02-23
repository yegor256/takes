/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.hamcrest;

import java.io.InputStream;
import java.nio.charset.Charset;
import org.cactoos.Text;
import org.cactoos.io.InputStreamOf;
import org.cactoos.text.TextOf;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.StringDescription;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link AbstractHmTextBody}.
 *
 * @since 2.0
 */
final class HmTextRsBodyTest {

    @Test
    void describesMismatchCorrectly() {
        final AbstractHmTextBody<Text> matcher = new HmTextBodyFake(
            new IsEqual<>("courage"), Charset.defaultCharset()
        );
        final StringDescription description = new StringDescription();
        final String body = "doubt";
        final Text item = new TextOf(body);
        matcher.matchesSafely(item);
        matcher.describeMismatchSafely(item, description);
        MatcherAssert.assertThat(
            description.toString(),
            new IsEqual<>(String.format("body was: %s", body))
        );
    }

    @Test
    void describesExpectedCorrectly() {
        final String expected = "red";
        final AbstractHmTextBody<Text> matcher = new HmTextBodyFake(
            new IsEqual<>(expected), Charset.defaultCharset()
        );
        final StringDescription description = new StringDescription();
        matcher.describeTo(description);
        MatcherAssert.assertThat(
            description.toString(),
            new IsEqual<>(String.format("body: \"%s\"", expected))
        );
    }

    /**
     * Fake child of {@link AbstractHmTextBody} for the test.
     * @since 2.0
     */
    private final class HmTextBodyFake extends AbstractHmTextBody<Text> {

        /**
         * Ctor.
         *
         * @param body Body matcher.
         * @param charset Charset of the text.
         */
        HmTextBodyFake(final Matcher<String> body, final Charset charset) {
            super(body, charset);
        }

        @Override
        public InputStream itemBody(final Text item) {
            return new InputStreamOf(item);
        }
    }
}
