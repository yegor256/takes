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
package org.takes.rq;

import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link org.takes.rq.RqMethod}.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.9.1
 */
public final class RqMethodTest {

    /**
     * RqMethod can return its method.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnsMethod() throws IOException {
        MatcherAssert.assertThat(
            new RqMethod.Base(new RqFake(RqMethod.POST)).method(),
            Matchers.equalTo(RqMethod.POST)
        );
    }

    /**
     * RqMethod supports all standard HTTP methods.
     * @throws IOException If some problem inside
     */
    @Test
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void supportsAllStandardMethods() throws IOException {
        for (final String method
            : Arrays.asList(
                RqMethod.DELETE, RqMethod.GET, RqMethod.HEAD, RqMethod.OPTIONS,
                RqMethod.PATCH, RqMethod.POST, RqMethod.PUT, RqMethod.TRACE,
                RqMethod.CONNECT
            )
        ) {
            MatcherAssert.assertThat(
                new RqMethod.Base(new RqFake(method)).method(),
                Matchers.equalTo(method)
            );
        }
    }

    /**
     * RqMethod supports extension methods.
     * @throws IOException If some problem inside
     */
    @Test
    public void supportsExtensionMethods() throws IOException {
        final String method = "CUSTOM";
        MatcherAssert.assertThat(
            new RqMethod.Base(new RqFake(method)).method(),
            Matchers.equalTo(method)
        );
    }

    /**
     * RqMethod can fail when request URI is missing.
     * @throws IOException If some problem inside
     */
    @Test(expected = IOException.class)
    public void failsOnMissingUri() throws IOException {
        new RqMethod.Base(new RqSimple(Arrays.asList("GET"), null)).method();
    }

    /**
     * RqMethod can fail when HTTP method line has any extra undefined
     * elements.
     * @throws IOException If some problem inside
     */
    @Test(expected = IOException.class)
    public void failsOnExtraLineElement() throws IOException {
        new RqMethod.Base(
            new RqSimple(Arrays.asList("GET / HTTP/1.1 abc"), null)
        ).method();
    }

    /**
     * RqMethod can fail when HTTP method line has any extra spaces
     * between the elements.
     * @throws IOException If some problem inside
     */
    @Test(expected = IOException.class)
    public void failsOnExtraSpaces() throws IOException {
        new RqMethod.Base(
            new RqSimple(Arrays.asList("GET /     HTTP/1.1"), null)
        ).method();
    }

    /**
     * RqMethod can fail when HTTP extension method name contains separator
     * characters.
     * @throws IOException If some problem inside
     */
    @Test(expected = IOException.class)
    public void failsOnSeparatorsInExtensionMethod() throws IOException {
        new RqMethod.Base(new RqFake("CUSTO{M)")).method();
    }
}
