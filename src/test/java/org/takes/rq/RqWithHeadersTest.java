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
package org.takes.rq;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.cactoos.text.JoinedText;
import org.junit.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.StartsWith;

/**
 * Test case for {@link RqWithHeaders}.
 * @since 1.0
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
public final class RqWithHeadersTest {

    /**
     * RqWithHeaders can add headers.
     */
    @Test
    public void addsHeadersToRequest() {
        final String testheader = "TestHeader: someValue";
        final String someheader = "SomeHeader: testValue";
        new Assertion<>(
            "Request must contain headers",
            () -> new RqPrint(
                new RqWithHeaders(
                    new RqFake(),
                    testheader,
                    someheader
                )
            ).print(),
            new StartsWith(
                new JoinedText(
                    "\r\n",
                    "GET /",
                    "Host: www.example.com",
                    testheader,
                    someheader
                )
            )
        ).affirm();
    }

    /**
     * Checks RqWithHeaders equals method.
     */
    @Test
    public void equalsAndHashCodeEqualTest() {
        EqualsVerifier.forClass(RqWithHeaders.class)
            .suppress(Warning.TRANSIENT_FIELDS)
            .withRedefinedSuperclass()
            .verify();
    }
}
