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
package org.takes.rq.form;

import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqBuffered;
import org.takes.rq.RqFake;
import org.takes.rq.RqForm;

/**
 * Test case for {@link RqFormBase}.
 * @since 0.33
 */
final class RqFormBaseTest {

    /**
     * Content-Length header template.
     */
    private static final String HEADER = "Content-Length: %d";

    @Test
    void parsesHttpBody() throws IOException {
        final String body = "alpha=a+b+c&beta=%20Yes%20";
        final RqForm req = new RqFormBase(
            new RqBuffered(
                new RqFake(
                    Arrays.asList(
                        "GET /h?a=3",
                        "Host: www.example.com",
                        String.format(
                            RqFormBaseTest.HEADER,
                            body.getBytes().length
                        )
                    ),
                    body
                )
            )
        );
        MatcherAssert.assertThat(
            req.param("beta"),
            Matchers.hasItem(" Yes ")
        );
        MatcherAssert.assertThat(
            req.names(),
            Matchers.hasItem("alpha")
        );
    }

    @Test
    void sameInstance() throws IOException {
        final RqForm req = new RqFormBase(
            new RqBuffered(
                new RqFake(
                    Arrays.asList(
                        "GET /path?a=3",
                        "Host: www.example2.com"
                    ),
                    "alpha=a+b+c&beta=%20No%20"
                )
            )
        );
        MatcherAssert.assertThat(
            req.names() == req.names(),
            Matchers.is(Boolean.TRUE)
        );
    }
}
