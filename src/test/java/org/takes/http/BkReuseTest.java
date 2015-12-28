/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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

import com.google.common.base.Joiner;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.matchers.RegexMatchers;
import java.net.Socket;
import java.net.URI;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.takes.tk.TkText;

/**
 * Test case for {@link BkReuse}.
 *
 * @author Piotr Pradzynski (prondzyn@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 * @todo #306:30min Implement missing BkReuse.accept method to support
 *  HTTP persistent connections and to pass below three tests. BkReuse.accept
 *  should handles more than one HTTP requests in one connection and return
 *  correct HTTP status when Content-Length is not specified.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class BkReuseTest {

    /**
     * BkReuse can handle two requests in one connection.
     * @throws Exception If some problem inside
     */
    @Ignore
    @Test
    public void handlesTwoRequestInOneConnection() throws Exception {
        final String text = "Hello world!";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                Joiner.on("\r\n").join(
                    "POST / HTTP/1.1",
                    "Host: localhost",
                    "Content-Length: 4",
                    "",
                    "hi",
                    "POST / HTTP/1.1",
                    "Host: localhost",
                    "Content-Length: 4",
                    "",
                    "hi"
                )
            )
        ).start();
        final URI uri = container.home();
        final Socket socket = new Socket(uri.getHost(), uri.getPort());
        new BkReuse(new BkBasic(new TkText(text))).accept(socket);
        container.stop();
        MatcherAssert.assertThat(
            socket.getOutputStream().toString(),
            RegexMatchers.containsPattern(text + ".*?" + text)
        );
    }

    /**
     * BkReuse can return HTTP status 411 when a persistent connection request
     * has no Content-Length.
     * @throws Exception If some problem inside
     */
    @Ignore
    @Test
    public void returnsProperResponseCodeOnNoContentLength() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                Joiner.on("\r\n").join(
                    "POST / HTTP/1.1",
                    "Host: localhost",
                    "",
                    "hi"
                )
            )
        ).start();
        final URI uri = container.home();
        final Socket socket = new Socket(uri.getHost(), uri.getPort());
        new BkReuse(new BkBasic(new TkText("411 Test"))).accept(socket);
        container.stop();
        MatcherAssert.assertThat(
            socket.getOutputStream().toString(),
            Matchers.containsString("HTTP/1.1 411 Length Required")
        );
    }

    /**
     * BkReuse can accept no content-length on closed connection.
     * @throws Exception If some problem inside
     */
    @Ignore
    @Test
    public void acceptsNoContentLengthOnClosedConnection() throws Exception {
        final String text = "Close Test";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                Joiner.on("\r\n").join(
                    "POST / HTTP/1.1",
                    "Host: localhost",
                    "Connection: Close",
                    "",
                    "hi"
                )
            )
        ).start();
        final URI uri = container.home();
        final Socket socket = new Socket(uri.getHost(), uri.getPort());
        new BkReuse(new BkBasic(new TkText(text))).accept(socket);
        container.stop();
        MatcherAssert.assertThat(
            socket.getOutputStream().toString(),
            Matchers.containsString(text)
        );
    }
}
