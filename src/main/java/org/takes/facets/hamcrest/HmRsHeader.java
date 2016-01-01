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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.takes.Response;

/**
 * Response Header Matcher.
 *
 * <p>This "matcher" tests given response header.
 * <p>The class is immutable and thread-safe.
 *
 * @author Eugene Kondrashev (eugene.kondrashev@gmail.com)
 * @author I. Sokolov (happy.neko@gmail.com)
 * @author Andrey Eliseev (aeg.exper0@gmail.com)
 * @version $Id$
 * @since 0.23.3
 */
public final class HmRsHeader extends TypeSafeMatcher<Response> {

    /**
     * Values string used in description of mismatches.
     */
    private static final String VALUES_STR = " -> values: ";

    /**
     * Header matcher.
     */
    private final transient Matcher<String> header;

    /**
     * Value matcher.
     */
    private final transient Matcher<Iterable<String>> value;

    /**
     * Mismatched header values.
     */
    private transient Collection<String> failed;

    /**
     * Ctor.
     * @param hdrm Header matcher
     * @param vlm Value matcher
     */
    public HmRsHeader(final Matcher<String> hdrm,
        final Matcher<Iterable<String>> vlm) {
        super();
        this.header = hdrm;
        this.value = vlm;
    }

    /**
     * Ctor.
     * @param hdr Header name
     * @param vlm Value matcher
     */
    public HmRsHeader(final String hdr,
        final Matcher<Iterable<String>> vlm) {
        this(Matchers.equalToIgnoringCase(hdr), vlm);
    }

    /**
     * Ctor.
     * @param hdr Header name
     * @param val Header value
     */
    public HmRsHeader(final String hdr, final String val) {
        this(
            Matchers.equalToIgnoringCase(hdr),
            Matchers.hasItems(val)
        );
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("header: ")
            .appendDescriptionOf(this.header)
            .appendText(HmRsHeader.VALUES_STR)
            .appendDescriptionOf(this.value);
    }

    @Override
    public boolean matchesSafely(final Response item) {
        try {
            final Iterator<String> headers = item.head().iterator();
            if (headers.hasNext()) {
                headers.next();
            }
            final List<String> values = new ArrayList<String>(0);
            while (headers.hasNext()) {
                final String[] parts = HmRsHeader.split(headers.next());
                if (this.header.matches(parts[0].trim())) {
                    values.add(parts[1].trim());
                }
            }
            final boolean result = this.value.matches(values);
            if (!result) {
                this.failed = values;
            }
            return result;
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void describeMismatchSafely(final Response item,
                                       final Description description) {
        description.appendText("header was: ")
            .appendDescriptionOf(this.header)
            .appendText(HmRsHeader.VALUES_STR)
            .appendValue(this.failed);
    }

    /**
     * Splits the given header to [name, value] array.
     * @param header Header
     * @return Array in which the first element is header name,
     *  the second is header value
     */
    private static String[] split(final String header) {
        return header.split(":", 2);
    }
}
