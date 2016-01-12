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
package org.takes.rq;

import java.io.IOException;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RqWithDefaultHeader}.
 * @author Andrey Eliseev (aeg.exper0@gmail.com)
 * @version $Id$
 */
public final class RqWithDefaultHeaderTest {
    /**
     * Default header name.
     */
    private static final String DEFAULT_HEADER = "X-Default-Header";
    /**
     * Default header value.
     */
    private static final String DEFAULT_HEADER_VALUE = "X-Default-Value";

    /**
     * RqWithDefaultHeader provides default header.
     * @throws IOException If some problem inside
     */
    @Test
    public void defaultHeader() throws IOException {
        MatcherAssert.assertThat(
            new RqPrint(
                new RqWithDefaultHeader(
                    new RqFake(),
                    DEFAULT_HEADER,
                    DEFAULT_HEADER_VALUE
                )
            ).print(),
            Matchers.containsString(
                String.format("%s: %s", DEFAULT_HEADER, DEFAULT_HEADER_VALUE)
            )
        );
    }

    /**
     * RqWithDefaultHeader doesn't override header value
     * if it's already exists.
     * @throws IOException If some problem inside
     */
    @Test
    public void notDefaultHeader() throws IOException {
        MatcherAssert.assertThat(
            new RqPrint(
                new RqWithDefaultHeader(
                    new RqWithHeader(
                        new RqFake(),
                        DEFAULT_HEADER,
                        "Non-Default-Value"
                    ),
                    DEFAULT_HEADER,
                    DEFAULT_HEADER_VALUE
                )
            ).print(),
            Matchers.containsString(
                String.format("%s: Non-Default-Value", DEFAULT_HEADER)
            )
        );
    }

    /**
     * Checks RqWithHeader equals method.
     * @throws Exception If some problem inside
     */
    @Test
    public void equalsAndHashCodeEqualTest() throws Exception {
        EqualsVerifier.forClass(RqWithHeader.class)
            .suppress(Warning.TRANSIENT_FIELDS)
            .withRedefinedSuperclass()
            .verify();
    }
}
