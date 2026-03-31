/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.it.fm;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.XmlResponse;
import com.jcabi.http.wire.VerboseWire;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link App}.
 * @since 0.16
 */
final class AppITCase {

    /**
     * Home with Takes server.
     */
    private static final String HOME = System.getProperty("takes.home");

    @Test
    void justWorks() throws Exception {
        Assumptions.assumeTrue(AppITCase.HOME != null);
        MatcherAssert.assertThat(
            "Response must be valid HTML document",
            new JdkRequest(String.format("%s/f", AppITCase.HOME))
                .through(VerboseWire.class)
                .fetch()
                .as(XmlResponse.class)
                .xml()
                .xpath("//xhtml:html"),
            Matchers.hasSize(1)
        );
    }
}
