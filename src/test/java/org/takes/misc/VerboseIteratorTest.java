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
package org.takes.misc;

import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Tests for {@link VerboseIterator}.
 * @author Marcus Sanchez (sanchez.marcus@gmail.com)
 * @version $Id$
 * @since 0.15.1
 */
public class VerboseIteratorTest {

    /**
     * VerboseIterator can return next value on a valid list.
     */
    @Test
    public final void returnsNextValue() {
        final String accept = "Accept: text/plain";
        MatcherAssert.assertThat(
            new VerboseIterable<String>(
                Arrays.asList(
                    accept,
                    "Accept-Charset: utf-8",
                    "Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==",
                    "Cache-Control: no-cache",
                    "From: user@example.com"
                ),
                "Empty Error Message"
            ).iterator().next(),
            Matchers.equalTo(accept)
        );
    }

    /**
     * VerboseIterator can inform has a next value on a valid list.
     */
    @Test
    public final void informsHasNextValue() {
        MatcherAssert.assertThat(
            new VerboseIterable<String>(
                Arrays.asList(
                    "User-Agent: LII-Cello/1.0 libwww/2.5",
                    "accept-extension = \";\"",
                    "Accept: text/*;",
                    "Accept-Encoding: gzip;q=1.0"
                ),
                "This error should not be thrown"
            ).iterator().hasNext(), Matchers.equalTo(true)
        );
    }

    /**
     * VerboseIterator next value throws exception on an empty list.
     */
    @Test(expected = RuntimeException.class)
    public final void nextValueThrowsExceptionOnEmptyList() {
        new VerboseIterable<String>(
            Collections.<String>emptyList(),
            "Expected Error Message"
        ).iterator().next();
    }

    /**
     * VerboseIterator returns false in has next value on empty list.
     */
    @Test
    public final void returnFalseInHasNextValueOnEmptyList() {
        MatcherAssert.assertThat(
            new VerboseIterable<String>(
                Collections.<String>emptyList(),
                "Non used Error Message"
            ).iterator().hasNext(),
            Matchers.equalTo(false)
        );
    }

    /**
     * VerboseIterator can remove a value.
     */
    @Test(expected = UnsupportedOperationException.class)
    public final void removeValue() {
        new VerboseIterable<String>(
            Arrays.asList(
                "If-None-Match: \"737060cd8c284d8af7ad3082f209582d\"",
                "If-Range: \"737060cd8c284d8af7ad3082f209582d\"",
                "Max-Forwards: 10",
                "Origin: http://www.example-social-network.com",
                "User-Agent: Mozilla/5.0 (X11; Linux x86_64) Gecko/2010010"
            ),
            "Thrown Error Message"
        ).iterator().remove();
    }
}
