package org.takes.facets.fallback;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.net.HttpURLConnection;

/**
 * Created by by Igor Piddubnyi (igor.piddubnyi@gmail.com).
 */
public final class FbLog4jTest {

    /**
     * FbLog4j can log a problem.
     * @throws Exception If some problem inside
     */
    @Test
    public void logsProblem() throws Exception {
        final RqFallback req = new RqFallback.Fake(
                HttpURLConnection.HTTP_NOT_FOUND
        );
        MatcherAssert.assertThat(
                new FbLog4j().route(req).has(),
                Matchers.is(false)
        );
    }
}
