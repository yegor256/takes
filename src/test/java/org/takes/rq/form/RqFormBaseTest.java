/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.form;

import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqBuffered;
import org.takes.rq.RqFake;
import org.takes.rq.RqForm;

/**
 * Test case for {@link RqFormBase}.
 * @since 0.33
 */
final class RqFormBaseTest {

    /**
     * Content-Length header template.
     */
    private static final String HEADER = "Content-Length: %d";

    @Test
    void parsesHttpBody() throws IOException {
        final String body = "alpha=a+b+c&beta=%20Yes%20";
        final RqForm req = new RqFormBase(
            new RqBuffered(
                new RqFake(
                    Arrays.asList(
                        "GET /h?a=3",
                        "Host: www.example.com",
                        String.format(
                            RqFormBaseTest.HEADER,
                            body.getBytes().length
                        )
                    ),
                    body
                )
            )
        );
        MatcherAssert.assertThat(
            "Form parameter beta must be URL-decoded properly",
            req.param("beta"),
            Matchers.hasItem(" Yes ")
        );
        MatcherAssert.assertThat(
            "Form parameter names must contain alpha",
            req.names(),
            Matchers.hasItem("alpha")
        );
    }

    @Test
    void sameInstance() throws IOException {
        final RqForm req = new RqFormBase(
            new RqBuffered(
                new RqFake(
                    Arrays.asList(
                        "GET /path?a=3",
                        "Host: www.example2.com"
                    ),
                    "alpha=a+b+c&beta=%20No%20"
                )
            )
        );
        MatcherAssert.assertThat(
            "Form names method must return same instance on multiple calls",
            req.names() == req.names(),
            Matchers.is(Boolean.TRUE)
        );
    }
}
