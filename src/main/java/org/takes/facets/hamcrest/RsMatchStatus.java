package org.takes.facets.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.takes.Response;

import java.io.IOException;

/**
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

    public RsMatchStatus(final Integer input) {
        super();
        this.expected = input;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Response Status same as: ").appendValue(this.expected);
    }

    @Override
    protected boolean matchesSafely(final Response item) {
        try {
            final String head = item.head().iterator().next();
            return head.contains(String.valueOf(this.expected));
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
