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
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.takes.Response;

/**
 * Response Status Matcher.
 *
 * <p>This "matcher" tests given response status code
 * <p>The class is immutable and thread-safe.
 *
 * @author Erim Erturk (erimerturk@gmail.com)
 * @version $Id$
 * @since 0.13
 */
public final class RsMatchStatus extends TypeSafeMatcher<Response> {

    /**
     * Expected response status code for matcher.
     */
    private final transient Integer expected;

    /**
     * Expected input.
     * @param input Is expected result code.
     */
    public RsMatchStatus(final Integer input) {
        super();
        this.expected = input;
    }

    /**
     * Fail description.
     * @param description Fail result description.
     */
    @Override
    public void describeTo(final Description description) {
        description.appendText("Response Status same as: ")
                .appendValue(this.expected);
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
            return head.contains(String.valueOf(this.expected));
        } catch (final IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

}
