/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.forward;

import java.io.IOException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.flash.RsFlash;

/**
 * Test case for {@link RsForward}.
 * @since 0.17
 */
final class RsForwardTest {

    @Test
    void buildsStackTrace() throws IOException {
        MatcherAssert.assertThat(
            ExceptionUtils.getFullStackTrace(
                new RsForward(new RsFlash(new IOException("the failure")))
            ),
            Matchers.containsString("failure")
        );
    }
}
