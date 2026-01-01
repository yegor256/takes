/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
