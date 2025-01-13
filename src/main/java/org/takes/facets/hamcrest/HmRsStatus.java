/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.takes.Response;

/**
 * Response Status Matcher.
 *
 * <p>This "matcher" tests given response status code.
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.13
 */
public final class HmRsStatus extends FeatureMatcher<Response, Integer> {

    /**
     * Description message.
     */
    private static final String FEATURE_NAME = "HTTP status code";

    /**
     * Create matcher using HTTP code.
     * @param val HTTP code value
     * @since 0.17
     */
    public HmRsStatus(final int val) {
        this(Matchers.equalTo(val));
    }

    /**
     * Create matcher using HTTP code matcher.
     * @param matcher HTTP code matcher
     */
    public HmRsStatus(final Matcher<Integer> matcher) {
        super(matcher, HmRsStatus.FEATURE_NAME, HmRsStatus.FEATURE_NAME);
    }

    @Override
    public Integer featureValueOf(final Response response) {
        try {
            final String head = response.head().iterator().next();
            final String[] parts = head.split(" ");
            return Integer.parseInt(parts[1]);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
