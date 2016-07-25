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
package org.takes.facets.fork;

import com.google.common.base.Joiner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link TkFork}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.4
 */
public final class TkForkTest {
    /**
     * Regular expression constant for FkRegex creation.
     */
    private static final String PATTERN = "/h[a-z]{2}";

    /**
     * TkFork can dispatch by regular expression.
     * @throws IOException If some problem inside
     */
    @Test
    public void dispatchesByRegularExpression() throws IOException {
        final String body = "hello, world!";
        MatcherAssert.assertThat(
            new RsPrint(
                new TkFork(new FkRegex(TkForkTest.PATTERN, body)).act(
                    new RqFake("GET", "/hey?yu", "")
                )
            ).print(),
            Matchers.equalTo(
                Joiner.on("\r\n").join(
                    "HTTP/1.1 200 OK",
                    String.format("Content-Length: %s", body.length()),
                    "Content-Type: text/plain",
                    "",
                    body
                )
            )
        );
    }

    /**
     * TkFork can equals with another TkFork.
     * @todo #697:30min/DEV The current implementation with lombok
     *  EqualsAndHashCode is not checking the in depth forks equals.
     */
    @Ignore
    @Test
    public void equalsWithAnotherTkFork() {
        final String body = "Test Equals";
        final List<Fork> same = new ArrayList<Fork>(2);
        same.add(new FkRegex(TkForkTest.PATTERN, body));
        same.add(new FkRegex("/", new TkEmpty()));
        final List<Fork> different = new ArrayList<Fork>(1);
        different.add(new FkRegex(TkForkTest.PATTERN, body));
        MatcherAssert.assertThat(
            new TkFork(same), Matchers.equalTo(new TkFork(same))
        );
        MatcherAssert.assertThat(
            new TkFork(same), Matchers.not(new TkFork(different))
        );
    }
}
