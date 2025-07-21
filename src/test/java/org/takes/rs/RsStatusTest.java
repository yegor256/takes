/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RsStatus}.
 * @since 1.22
 */
final class RsStatusTest {

    @Test
    void readsStatusCode() throws IOException {
        final int status = 200;
        MatcherAssert.assertThat(
            "Status reader must return the set status code",
            new RsStatus.Base(new RsWithStatus(new RsEmpty(), status)).status(),
            Matchers.equalTo(status)
        );
    }
}
