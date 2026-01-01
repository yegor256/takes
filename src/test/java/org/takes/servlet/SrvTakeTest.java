/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.servlet;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import jakarta.servlet.ServletContext;
import java.net.HttpURLConnection;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsText;

/**
 * Test case for {@link SrvTake}.
 *
 * @since 1.16
 */
final class SrvTakeTest {
    /**
     * Message returned if servlet Takes is executed correctly.
     */
    private static final String MSG =
        "Hello servlet! Using [%s] ServletContext.";

    @Test
    @Disabled
    void executeATakesAsAServlet() throws Exception {
        final String name = "webapp";
        final HttpServer server = HttpServer.createSimpleServer("./", 18_080);
        final WebappContext context = new WebappContext(name);
        final ServletRegistration servlet = context.addServlet(
            "takes",
            SrvTake.class
        );
        servlet.setInitParameter("take", TkApp.class.getName());
        servlet.addMapping("/test");
        context.deploy(server);
        server.start();
        new JdkRequest("http://localhost:18080/test")
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .assertBody(
                new StringContains(
                    new FormattedText(
                        SrvTakeTest.MSG,
                        name
                    ).asString()
                )
            );
        server.shutdownNow();
    }

    /**
     * Fake TkApp (for {@link SrvTake} test only).
     *
     * @since 1.16
     */
    public final class TkApp implements Take {
        /**
         * ServletContext.
         */
        private final ServletContext context;

        /**
         * Ctor.
         * @param ctx A ServletContext
         */
        TkApp(final ServletContext ctx) {
            this.context = ctx;
        }

        @Override
        public Response act(final Request req) {
            return new RsText(
                new UncheckedText(
                    new FormattedText(
                        SrvTakeTest.MSG,
                        this.context.getServletContextName()
                    )
                ).asString()
            );
        }
    }
}
