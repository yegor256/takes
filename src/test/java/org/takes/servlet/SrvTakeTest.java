/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.takes.servlet;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.net.HttpURLConnection;
import org.cactoos.text.FormattedText;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link SrvTake}.
 *
 * @since 1.16
 */
public final class SrvTakeTest {
    @Test
    public void executeATakesAsAServlet() throws Exception {
        final String name = "webapp";
        final HttpServer server = HttpServer.createSimpleServer("./", 18080);
        final WebappContext context = new WebappContext(name);
        final ServletRegistration servlet = context.addServlet(
            "takes",
            SrvTake.class
        );
        servlet.setInitParameter("take", TkServletApp.class.getName());
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
                        "Hello servlet! Using [%s] ServletContext.",
                        name
                    ).asString()
                )
            );
        server.shutdownNow();
    }
}
