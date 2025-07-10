/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.Response;
import org.takes.rq.RqFake;
import org.takes.rs.RsEmpty;

/**
 * Test of {@link PsAll}.
 * @since 0.22
 */
final class PsAllTest {

    @Test
    void thereShouldBeAtLeastOnePass() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> MatcherAssert.assertThat(
                new PsAll(
                    new ArrayList<>(0),
                    0
                ).enter(new RqFake()).has(),
                new IsEqual<>(false)
            )
        );
    }

    @Test
    void indexMustBeNonNegative() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> MatcherAssert.assertThat(
                new PsAll(
                    Collections.singletonList(new PsFake(true)),
                    -1
                ).enter(new RqFake()).has(),
                new IsEqual<>(false)
            )
        );
    }

    @Test
    void indexMustBeSmallEnough() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> MatcherAssert.assertThat(
                new PsAll(
                    Arrays.asList(
                        new PsFake(true),
                        new PsFake(false)
                    ),
                    2
                ).enter(new RqFake()).has(),
                new IsEqual<>(false)
            )
        );
    }

    @Test
    void testOneSuccessful() throws Exception {
        MatcherAssert.assertThat(
            new PsAll(
                Collections.singletonList(new PsFake(true)),
                0
            ).enter(new RqFake()).has(),
            new IsEqual<>(true)
        );
    }

    @Test
    void testOneFail() throws Exception {
        MatcherAssert.assertThat(
            new PsAll(
                Collections.singletonList(new PsFake(false)),
                0
            ).enter(new RqFake()).has(),
            new IsEqual<>(false)
        );
    }

    @Test
    void testSuccessfulIdx() throws Exception {
        final int index = 3;
        final Pass resulting = new PsFixed(
            new Identity.Simple("urn:foo:test")
        );
        final RqFake request = new RqFake();
        MatcherAssert.assertThat(
            new PsAll(
                Arrays.asList(
                    new PsFake(true),
                    new PsFake(true),
                    new PsFake(true),
                    resulting
                ),
                index
            ).enter(request).get().urn(),
            new IsEqual<>(resulting.enter(request).get().urn())
        );
    }

    @Test
    void testFail() throws Exception {
        MatcherAssert.assertThat(
            new PsAll(
                Arrays.asList(
                    new PsFake(true),
                    new PsFake(true),
                    new PsFake(false),
                    new PsFake(true)
                ),
                1
            ).enter(new RqFake()).has(),
            new IsEqual<>(false)
        );
    }

    @Test
    void exits() throws Exception {
        final Response response = new RsEmpty();
        final PsFake exiting = new PsFake(true);
        MatcherAssert.assertThat(
            new PsAll(
                Arrays.asList(
                    new PsFake(true),
                    exiting
                ),
                1
            ).exit(response, exiting.enter(new RqFake()).get()),
            new IsEqual<>(response)
        );
    }
}
