/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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
package org.takes.http;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.tk.TkFixed;

/**
 * Test case for {@link MainRemote}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.23
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class MainRemoteTest {

    /**
     * MainRemote can work.
     * @throws Exception If some problem inside
     */
    @Test
    public void startsAndStopsApp() throws Exception {
        new MainRemote(MainRemoteTest.DemoApp.class).exec(
            new MainRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    new JdkRequest(home)
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .assertBody(Matchers.startsWith("works"));
                }
            }
        );
    }

    /**
     * MainRemote passes additional arguments.
     * @throws Exception If some problem inside
     */
    @Test
    public void passesArgumentsToApp() throws Exception {
        final String[] args = {"works well!"};
        new MainRemote(MainRemoteTest.DemoAppArgs.class, args).exec(
            new MainRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    new JdkRequest(home)
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .assertBody(Matchers.startsWith("works well"));
                }
            }
        );
    }

    /**
     * Demo app.
     */
    public static final class DemoApp {
        /**
         * Ctor.
         */
        private DemoApp() {
            // it's a utility class
        }
        /**
         * Main entry point.
         * @param args Command line args
         * @throws IOException If fails
         */
        public static void main(final String... args) throws IOException {
            new FtCli(new TkFixed("works fine!"), args).start(Exit.NEVER);
        }
    }

    /**
     * Demo app.
     */
    public static final class DemoAppArgs {
        /**
         * Ctor.
         */
        private DemoAppArgs() {
            // it's a utility class
        }
        /**
         * Main entry point.
         * @param args Command line args
         * @throws IOException If fails
         */
        public static void main(final String... args) throws IOException {
            new FtCli(new TkFixed(args[1]), args[0]).start(Exit.NEVER);
        }
    }

}
