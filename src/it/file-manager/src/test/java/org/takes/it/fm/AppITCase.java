/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.it.fm;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.XmlResponse;
import com.jcabi.http.wire.VerboseWire;
import java.net.HttpURLConnection;
import org.junit.jupiter.api.Assertions;
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
        Assertions.assertNotNull(AppITCase.HOME);
        new JdkRequest(String.format("%s/f", AppITCase.HOME))
            .through(VerboseWire.class)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(XmlResponse.class)
            .assertXPath("//xhtml:html");
    }
}
