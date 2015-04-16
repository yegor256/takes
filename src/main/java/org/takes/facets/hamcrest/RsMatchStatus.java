package org.takes.facets.hamcrest;

import java.io.IOException;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.takes.Response;

/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
 */

/**
 *
 * Response Status Matcher.
 *
 * <p>This "matcher" tests given response status code
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Erim Erturk (erimerturk@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class RsMatchStatus extends TypeSafeMatcher<Response> {

    private final Integer expected;

    /**
     *
     * @param input is expected result code
     */
    public RsMatchStatus(final Integer input) {
        super();
        this.expected = input;
    }

    /**
     *
     * @param description fail result description
     */
    @Override
    public void describeTo(final Description description) {
        description
                .appendText("Response Status same as: ")
                .appendValue(this.expected);
    }

    /**
     *
     * @param item is tested element
     * @return true when expected type matched
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
