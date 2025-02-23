/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.form;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rq.RqForm;

/**
 * Test case for {@link RqFormSmart}.
 * @since 0.33
 */
final class RqFormSmartTest {

    @Test
    void parsesOneArgumentInBody() throws IOException {
        final RqForm req = new RqFormBase(
            new RqFake(
                "GET /just-a-test",
                "Host: www.takes.org",
                "test-6=blue"
            )
        );
        MatcherAssert.assertThat(
            new RqFormSmart(req).single("test-6"),
            Matchers.equalTo("blue")
        );
    }
}
