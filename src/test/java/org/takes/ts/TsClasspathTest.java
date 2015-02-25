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
package org.takes.ts;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Takes;
import org.takes.rq.RqPlain;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TsClasspath}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
public final class TsClasspathTest {

    /**
     * TsClasspath can dispatch by resource name.
     * @throws IOException If some problem inside
     */
    @Test
    public void dispatchesByResourceName() throws IOException {
        MatcherAssert.assertThat(
            new RsPrint(
                new TsClasspath().take(
                    new RqPlain(
                        "GET", "/org/takes/ts/TsClasspathTest.class", ""
                    )
                ).print()
            ).print(),
            Matchers.startsWith("HTTP/1.1 200 OK")
        );
    }

    /**
     * TsClasspath can throw when resource not found.
     * @throws IOException If some problem inside
     */
    @Test(expected = Takes.NotFoundException.class)
    public void throwsWhenResourceNotFound() throws IOException {
        new TsClasspath().take(
            new RqPlain("PUT", "/something-else", "")
        );
    }

}
