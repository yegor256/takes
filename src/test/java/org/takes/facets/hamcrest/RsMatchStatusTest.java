package org.takes.facets.hamcrest;

import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.rq.RqFake;
import org.takes.tk.TkEmpty;
import org.takes.tk.TkHTML;

/**
 *
 * Test case for {@link RsMatchStatus}.
 * @author Erim Erturk (erimerturk@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class RsMatchStatusTest {

    /**
     * should test response status code equal to expected code
     * @throws IOException
     */
    @Test
    public void responseCodeIsEqualToExpected() throws IOException {
        MatcherAssert.assertThat(
                new TkHTML("<html></html>").act(new RqFake()),
                Matchers.is(new RsMatchStatus(HttpURLConnection.HTTP_OK))
        );
        MatcherAssert.assertThat(
                new TkEmpty().act(new RqFake()),
                Matchers.is(new RsMatchStatus(HttpURLConnection.HTTP_OK))
        );
    }

    /**
     * should test expected code not equal case
     * @throws IOException
     */
    @Test
    public void responseCodeIsNotEqualToExpected() throws IOException {
        MatcherAssert.assertThat(
                new TkHTML("<html><body/></html>").act(new RqFake()),
                Matchers.not(Matchers.is(new RsMatchStatus(HttpURLConnection.HTTP_NOT_FOUND)))
        );
    }

}
