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
package org.takes.facets.hamcrest;

import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.takes.Response;

/**
 * Response Status Matcher.
 *
 * <p>This "matcher" tests given response status code.
 * <p>The class is immutable and thread-safe.
 *
 * @author Erim Erturk (erimerturk@gmail.com)
 * @version $Id$
 * @since 0.13
 */
public final class HmRsStatus extends TypeSafeMatcher<Response> {

    /**
     * Expected response status code matcher.
     */
    private final transient Matcher<? extends Number> matcher;

    /**
     * Expected matcher.
     * @param val Value
     * @since 0.17
     */
    public HmRsStatus(final int val) {
        this(CoreMatchers.equalTo(val));
    }

    /**
     * Expected matcher.
     * @param mtchr Is expected result code matcher.
     */
    public HmRsStatus(final Matcher<? extends Number> mtchr) {
        super();
        this.matcher = mtchr;
    }

    /**
     * Fail description.
     * @param description Fail result description.
     */
    @Override
    public void describeTo(final Description description) {
        this.matcher.describeTo(description);
    }

    /**
     * Type safe matcher.
     * @param item Is tested element
     * @return True when expected type matched.
     */
    @Override
    public boolean matchesSafely(final Response item) {
        try {
            final String head = item.head().iterator().next();
            final String[] parts = head.split(" ");
            return this.matcher.matches(Integer.parseInt(parts[1]));
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
