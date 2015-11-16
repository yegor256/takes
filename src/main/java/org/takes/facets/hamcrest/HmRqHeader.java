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
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.takes.Request;
import org.takes.misc.EntryImpl;
import org.takes.rq.RqHeaders;

/**
 * Response Header Matcher.
 *
 * <p>This "matcher" tests given response header.
 * <p>The header name(s) should be provided in lowercase.
 * <p>The class is immutable and thread-safe.
 *
 * @author Eugene Kondrashev (eugene.kondrashev@gmail.com)
 * @version $Id$
 * @since 0.23.3
 */
public final class HmRqHeader extends TypeSafeMatcher<Request> {

    /**
     * Expected request header matcher.
     */
    private final transient HeaderMatcher matcher;

    /**
     * Ctor.
     * @param mtchr Matcher
     */
    public HmRqHeader(
        final Matcher<? extends Map.Entry<String, String>> mtchr) {
        super();
        this.matcher = new EntryHeaderMatcher(mtchr);
    }

    /**
     * Ctor.
     * @param header Header name
     * @param mtchr Matcher
     */
    public HmRqHeader(final String header,
        final Matcher<? extends Iterable<String>> mtchr) {
        super();
        this.matcher = new IterableHeaderMatcher(header, mtchr);
    }

    /**
     * Ctor.
     * @param header Header name
     * @param value Header value
     */
    public HmRqHeader(final String header, final String value) {
        this(new EntryMatcher<String, String>(header, value));
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
            return this.matcher.matches(new RqHeaders.Base(item));
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Header matcher.
     */
    private interface HeaderMatcher {

        /**
         * Performs the matching.
         *
         * @param headers Headers to check
         * @return True if positive match
         * @throws IOException If fails
         */
        boolean matches(final RqHeaders headers) throws IOException;

        /**
         * Generates a description of the matcher.
         *
         * @param description The description to be built or appended to
         */
        void describeTo(final Description description);
    }

    /**
     * Header matcher for {@code Matcher<? extends Map.Entry<String, String>>}.
     */
    private static class EntryHeaderMatcher implements HeaderMatcher {

        /**
         * Matcher.
         */
        private final transient
            Matcher<? extends Map.Entry<String, String>> matcher;

        /**
         * Ctor.
         * @param mtchr Matcher
         */
        public EntryHeaderMatcher(
            final Matcher<? extends Entry<String, String>> mtchr) {
            this.matcher = mtchr;
        }

        @Override
        @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
        public boolean matches(final RqHeaders headers) throws IOException {
            boolean result = false;
            for (final String name : headers.names()) {
                for (final String value: headers.header(name)) {
                    final Map.Entry<String, String> entry =
                        new EntryImpl<String, String>(
                            name.trim().toLowerCase(Locale.ENGLISH),
                            value.trim()
                        );
                    if (this.matcher.matches(entry)) {
                        result = true;
                        break;
                    }
                }
                if (result) {
                    break;
                }
            }
            return result;
        }

        @Override
        public void describeTo(final Description description) {
            this.matcher.describeTo(description);
        }
    }

    /**
     * Header matcher for {@code Matcher<? extends Iterable<String>>}.
     */
    private static class IterableHeaderMatcher implements HeaderMatcher {

        /**
         * Header.
         */
        private final transient String header;

        /**
         * Matcher.
         */
        private final transient Matcher<? extends Iterable<String>> matcher;

        /**
         * Ctor.
         * @param hdr Header
         * @param mtchr Matcher
         */
        public IterableHeaderMatcher(final String hdr,
            final Matcher<? extends Iterable<String>> mtchr) {
            this.header = hdr;
            this.matcher = mtchr;
        }

        @Override
        public boolean matches(final RqHeaders headers) throws IOException {
            final Collection<String> hdrs = new LinkedList<String>();
            for (final String name : headers.names()) {
                final String lower = name.trim().toLowerCase(Locale.ENGLISH);
                if (lower.equals(this.header)) {
                    for (final String value : headers.header(name)) {
                        hdrs.add(value);
                    }
                }
            }
            return this.matcher.matches(hdrs);
        }

        @Override
        public void describeTo(final Description description) {
            this.matcher.describeTo(description);
        }
    }
}
