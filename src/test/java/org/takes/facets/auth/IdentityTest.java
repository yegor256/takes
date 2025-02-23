/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Identity}.
 * @since 0.17
 */
final class IdentityTest {

    @Test
    void equalsToItself() {
        MatcherAssert.assertThat(
            Identity.ANONYMOUS,
            new IsEqual<>(Identity.ANONYMOUS)
        );
    }

}
