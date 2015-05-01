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
package org.takes.misc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableWithSize;
import org.junit.Test;

/**
 * Tests for {@link VerboseIterator}.
 * @author marcus.sanchez (sanchezmarcus@gmail.com)
 * @version $Id$
 * @since 0.15.1
 */
public class VerboseIteratorTest {

    /**
     * VerboseIterator can return next value on a valid list.
     */
    @Test
    public final void returnsNextValue() {
        MatcherAssert.assertThat(
            new VerboseIterable<String>(
                Arrays.asList(
                    "Accept: text/plain",
                    "Accept-Charset: utf-8",
                    "Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==",
                    "Cache-Control: no-cache",
                    "From: user@example.com"
                ),
                "Empty Error Message"
            ).iterator().next(),
            Matchers.equalTo("Accept: text/plain")
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
                    "Accept: text/plain",
                    "Accept-Charset: utf-8",
                    "Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==",
                    "From: user@example.com"
                ),
                "Empty Error Message"
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
            "Empty Error Message"
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
                "Empty Error Message"
            ).iterator().hasNext(),
            Matchers.equalTo(false)
        );
    }

    /**
     * VerboseIterator can remove a value.
     */
    @Test(expected = UnsupportedOperationException.class)
    public final void removeValue() {
        final List<String> valid = Arrays.asList(
            "Accept: text/plain",
            "Accept-Charset: utf-8",
            "Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==",
            "Cache-Control: no-cache",
            "From: user@example.com"
        );
        VerboseIterable<String> verbose = new VerboseIterable<String>(
            valid, "Empty Error Message"
        );
        verbose.iterator().remove();
        MatcherAssert.assertThat(
            verbose,
            IsIterableWithSize.<String>iterableWithSize(valid.size() - 1)
        );
    }
}
