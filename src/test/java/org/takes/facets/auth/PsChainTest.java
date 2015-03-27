package org.takes.facets.auth;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsEmpty;

import java.io.IOException;

/**
 * Test case for {@link PsChain}.
 * @author Aleksey Kurochka (eg04lt3r@gmail.com)
 * @version $Id$
 */
public final class PsChainTest {

    /**
     * Check that PsChain returns proper identity
     * @throws IOException
     */
    @Test
    public void chainExecutionTest() throws IOException {
        MatcherAssert.assertThat(
                new PsChain(
                        new PsLogout(),
                        new PsFake(true)
                ).enter(new RqFake()).next(),
                Matchers.is(Identity.ANONYMOUS)
        );
    }

    /**
     * Check that exit method of PsChain returns proper response
     * @throws IOException
     */
    @Test
    public void exitChainTest() throws IOException {
        MatcherAssert.assertThat(
                new PsChain(
                        new PsFake(true)
                ).exit(new RsEmpty(), Identity.ANONYMOUS).head().iterator().next(),
                Matchers.containsString("HTTP/1.1 200 O")
        );
    }
}
