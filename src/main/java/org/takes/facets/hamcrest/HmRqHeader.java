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
import java.util.Collections;
import java.util.Map.Entry;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.takes.Request;
import org.takes.rq.RqHeaders;

/**
 * Response Header Matcher.
 *
 * <p>This "matcher" tests given response header.
 * <p>The class is immutable and thread-safe.
 *
 * @author Eugene Kondrashev (eugene.kondrashev@gmail.com)
 * @version $Id$
 * @since 0.16.2
 */
public final class HmRqHeader extends TypeSafeMatcher<Request> {

    /**
     * Expected request header matcher.
     */
    private final transient Matcher<? extends Entry<String, String>> matcher;

    /**
     * Expected matcher.
     * @param mtchr Is expected header matcher.
     */
    public HmRqHeader(final Matcher<? extends Entry<String, String>> mtchr) {
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
    public boolean matchesSafely(final Request item) {
        try {
            final RqHeaders headers = new RqHeaders.Base(item);
            for (final String name: headers.names()) {
                for (final String value: headers.header(name)) {
                    if (this.matcher.matches(
                            Collections.singletonMap(name, value)
                    )) {
                        return true;
                    }
                }
            }
            return false;
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
