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

import java.net.URLEncoder;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rq.RqForm;

/**
 * Test case for {@link RqFormFake}.
 * @since 0.33
 */
final class RqFormFakeTest {

    /**
     * Content-Length header template.
     */
    private static final String HEADER = "Content-Length: %d";

    @Test
    void createsFormRequestWithParams() throws Exception {
        final String key = "key";
        final String akey = "anotherkey";
        final String value = "value";
        final String avalue = "a&b";
        final String aavalue = "againanothervalue";
        final RqForm req = new RqFormFake(
            new RqFake(
                new ListOf<>(
                    "GET /form",
                    "Host: www.example5.com",
                    String.format(
                        RqFormFakeTest.HEADER,
                        URLEncoder.encode(
                            "key=value&key=a&b&anotherkey=againanothervalue",
                            "UTF-8"
                        ).length()
                    )
                ),
                ""
            ),
            key, value,
            key, avalue,
            akey, aavalue
        );
        MatcherAssert.assertThat(
            req.param(key),
            Matchers.hasItems(value, avalue)
        );
        MatcherAssert.assertThat(
            req.param(akey),
            Matchers.hasItems(aavalue)
        );
        MatcherAssert.assertThat(
            req.names(),
            Matchers.hasItems(key, akey)
        );
    }

    @Test
    void throwsExceptionWhenNotCorrectlyCreated() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new RqFormFake(
                new RqFake(),
                "param"
            )
        );
    }
}
