package org.takes.misc;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Test;

import java.time.Instant;

/**
 * Tests of {@link Expires} interface and direct implementations.
 */
public class ExpiresTest {

    @Test
    public void returnsNever() {
        MatcherAssert.assertThat(
            "Wrong expiration time for never",
            new Expires.Never().print(),
            new IsEqual<>("Expires=Thu, 01 Jan 1970 00:00:00 GMT")
        );
    }

    @Test
    public void returnsHour() {

        MatcherAssert.assertThat(
            "Wrong expiration time for Hour",
            new Expires.Hour(new Expires.Date(Instant.ofEpochSecond(3600).toEpochMilli())).print(),
            new IsEqual<>("Expires=Thu, 01 Jan 1970 00:00:00 GMT")
        );
    }
}
