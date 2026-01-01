/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.misc;

import java.util.Arrays;
import java.util.Collections;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link VerboseIterator}.
 * @since 0.15.1
 */
final class VerboseIteratorTest {

    @Test
    void returnsNextValue() {
        final String accept = "Accept: text/plain";
        MatcherAssert.assertThat(
            "Iterator must return the first element from non-empty list",
            new VerboseIterable<>(
                Arrays.asList(
                    accept,
                    "Accept-Charset: utf-8",
                    "Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==",
                    "Cache-Control: no-cache",
                    "From: user@example.com"
                ),
                new TextOf("Empty Error Message")
            ).iterator().next(),
            Matchers.equalTo(accept)
        );
    }

    @Test
    void informsHasNextValue() {
        MatcherAssert.assertThat(
            "Iterator must indicate next element exists for non-empty list",
            new VerboseIterable<>(
                Arrays.asList(
                    "User-Agent: LII-Cello/1.0 libwww/2.5",
                    "accept-extension = \";\"",
                    "Accept: text/*;",
                    "Accept-Encoding: gzip;q=1.0"
                ),
                new TextOf("This error should not be thrown")
            ).iterator().hasNext(), Matchers.equalTo(true)
        );
    }

    @Test
    void nextValueThrowsExceptionOnEmptyList() {
        Assertions.assertThrows(
            RuntimeException.class,
            () -> new VerboseIterable<String>(
                Collections.emptyList(),
                new TextOf("Expected Error Message")
            ).iterator().next()
        );
    }

    @Test
    void returnFalseInHasNextValueOnEmptyList() {
        MatcherAssert.assertThat(
            "Iterator must indicate no next element exists for empty list",
            new VerboseIterable<String>(
                Collections.emptyList(),
                new TextOf("Non used Error Message")
            ).iterator().hasNext(),
            Matchers.equalTo(false)
        );
    }

    @Test
    void removeValue() {
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> new VerboseIterable<>(
                Arrays.asList(
                    "If-None-Match: \"737060cd8c284d8af7ad3082f209582d\"",
                    "If-Range: \"737060cd8c284d8af7ad3082f209582d\"",
                    "Max-Forwards: 10",
                    "Origin: http://www.example-social-network.com",
                    "User-Agent: Mozilla/5.0 (X11; Linux x86_64) Gecko/2010010"
                ),
                new TextOf("Thrown Error Message")
            ).iterator().remove()
        );
    }
}
