/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.misc;

import java.time.Instant;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link Expires} interface and direct implementations.
 * @since 2.0
 */
final class ExpiresTest {

    /**
     * {@link Expires.Expired} can return expired time (0).
     */
    @Test
    void returnsExpired() {
        MatcherAssert.assertThat(
            "Wrong expiration time for expired time",
            new Expires.Expired().print(),
            new IsEqual<>("Expires=0")
        );
    }

    /**
     * {@link Expires.Never} can return epoch time.
     */
    @Test
    void returnsNever() {
        MatcherAssert.assertThat(
            "Wrong expiration time for never",
            new Expires.Never().print(),
            new IsEqual<>("Expires=Thu, 01 Jan 1970 00:00:00 GMT")
        );
    }

    /**
     * {@link Expires.Hour} can return expiration time of an hour.
     */
    @Test
    void returnsHour() {
        MatcherAssert.assertThat(
            "Wrong expiration time for Hour",
            new Expires.Hour(
                //@checkstyle MagicNumberCheck (1 line)
                new Expires.Date(Instant.ofEpochSecond(3600).toEpochMilli())
            ).print(),
            new IsEqual<>("Expires=Thu, 01 Jan 1970 01:00:00 GMT")
        );
    }

    /**
     * Date can return expires date string in GMT.
     */
    @Test
    void returnsExpiresStringInGmt() {
        MatcherAssert.assertThat(
            "Wrong expiration time in GMT",
            // @checkstyle MagicNumberCheck (2 lines)
            new Expires.Date(
                1_517_048_057_117L
            ).print(),
            new IsEqual<>("Expires=Sat, 27 Jan 2018 10:14:17 GMT")
        );
    }
}
