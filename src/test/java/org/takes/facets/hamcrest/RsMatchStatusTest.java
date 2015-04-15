package org.takes.facets.hamcrest;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.rq.RqFake;
import org.takes.tk.TkEmpty;
import org.takes.tk.TkHTML;

import java.io.IOException;

/**
 * Test case for {@link RsMatchStatus}.
 * @author Erim Erturk (erimerturk@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class RsMatchStatusTest {

    @Test
    public void shouldNotFailWhenExpectedResponseStatusCodeIsEqual() throws IOException {

        MatcherAssert.assertThat(
                new TkHTML("<html></html>").act(new RqFake()),
                Matchers.is(new RsMatchStatus(200))
        );

        MatcherAssert.assertThat(
                new TkEmpty().act(new RqFake()),
                Matchers.is(new RsMatchStatus(200))
        );

    }

    @Test
    public void shouldReturnFalseWhenExpectedResponseStatusCodeIsNotEqual() throws IOException {

        MatcherAssert.assertThat(
                new TkHTML("<html></html>").act(new RqFake()),
                Matchers.not(Matchers.is(new RsMatchStatus(404)))
        );

    }

}