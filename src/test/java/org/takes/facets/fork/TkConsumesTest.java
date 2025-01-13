/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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

import java.util.Arrays;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.StartsWith;
import org.takes.HttpException;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsJson;
import org.takes.rs.RsPrint;
import org.takes.tk.TkFixed;
import org.takes.tk.TkText;

/**
 * Test case for {@link TkConsumes}.
 * @since 1.0
 */
final class TkConsumesTest {

    /**
     * Application json.
     */
    private static final String APPLICATION_JSON = "application/json";

    @Test
    void acceptsCorrectContentTypeRequest() throws Exception {
        final String contenttype = "Content-Type: application/json";
        final Take consumes = new TkConsumes(
            new TkFixed(new RsJson(new RsEmpty())),
            TkConsumesTest.APPLICATION_JSON
        );
        final Response response = consumes.act(
            new RqFake(
                Arrays.asList(
                    "GET /?TkConsumes",
                    contenttype
                ),
                ""
            )
        );
        MatcherAssert.assertThat(
            new RsPrint(response),
            new StartsWith(
                new Joined(
                    "\r\n",
                    "HTTP/1.1 200 OK",
                    contenttype
                )
            )
        );
    }

    @Test
    void failsOnUnsupportedAcceptHeader() {
        Assertions.assertThrows(
            HttpException.class,
            () -> {
                final Take consumes = new TkConsumes(
                    new TkFixed(new RsJson(new RsEmpty())),
                    TkConsumesTest.APPLICATION_JSON
                );
                consumes.act(
                    new RqFake(
                        Arrays.asList(
                            "GET /?TkConsumes error",
                            "Content-Type: application/xml"
                        ),
                        ""
                    )
                ).head();
            }
        );
    }

    @Test
    void equalsAndHashCodeEqualTest() {
        final Take take = new TkText("text");
        final String type = "Content-Type: text/plain";
        new Assertion<>(
            "Must evaluate true equality",
            new TkConsumes(take, type),
            new IsEqual<>(new TkConsumes(take, type))
        ).affirm();
    }
}
