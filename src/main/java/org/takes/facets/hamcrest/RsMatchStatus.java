package org.takes.facets.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
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
public final class RsMatchStatus extends BaseMatcher<Response> {

    private Integer actual;

    public RsMatchStatus(Integer actual) {
        this.actual = actual;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Response Status same as: ").appendValue(actual);
    }

    @Override
    public void describeMismatch(final Object item, final Description description) {
        description.appendText("was ").appendValue(item);
    }

    @Override
    public boolean matches(final Object item) {

        if (!(item instanceof Response)) {
            return false;
        }

        try {
            final String head = ((Response) item).head().iterator().next();
            return head.contains(String.valueOf(actual));
        } catch (IOException e) {
            return false;
        }

    }
}
