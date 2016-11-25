/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Yegor Bugayenko
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

package org.takes.tk;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;

/**
 * Test case for {@link TkSlf4j}.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.11.2
 * @todo #712:30min Refactor this class to reduce the data abstraction coupling
 * in order to get rid of the checkstyle suppression of
 * ClassDataAbstractionCouplingCheck.
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class TkSlf4jTest {

    /**
     * TkSlf4j can log message.
     * @throws IOException If some problem inside
     */
    @Test
    public void logsMessage() throws IOException {
        new TkSlf4j(new TkText("test")).act(new RqFake());
    }

    /**
     * TkSlf4j can log exception.
     * @throws IOException If some problem inside
     */
    @Test(expected = IOException.class)
    public void logsException() throws IOException {
        new TkSlf4j(new TkFailure(new IOException(""))).act(new RqFake());
    }

    /**
     * {@link TkSlf4j} can output an empty body for {@link TkEmpty}.
     * @throws IOException if some I/O problem occurred.
     */
    @Test
    public void testEmptyResponseBody() throws IOException {
        new FtRemote(
            new TkSlf4j(new TkEmpty())
        ).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    new JdkRequest(home)
                        .method("POST")
                        .body().set("testEmptyResponseBody").back()
                        .fetch()
                        .as(RestResponse.class)
                        .assertBody(Matchers.isEmptyOrNullString())
                        .assertStatus(HttpURLConnection.HTTP_OK);
                }
            }
        );
    }
}
