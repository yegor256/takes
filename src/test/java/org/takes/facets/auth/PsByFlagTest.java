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
package org.takes.facets.auth;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.takes.Response;
import org.takes.rq.RqFake;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWithType;

/**
 * Test case for {@link PsByFlag}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.10
 */
public final class PsByFlagTest {
    /**
     * PsByFlag can skip if nothing found.
     * @throws IOException If some problem inside
     */
    @Test
    public void skipsIfNothingFound() throws IOException {
        MatcherAssert.assertThat(
            new PsByFlag(
                new PsByFlag.Pair(
                    "test", new PsFake(true)
                )
            ).enter(
                new RqFake("GET", "/?PsByFlag=x")
            ).hasNext(),
            Matchers.is(false)
        );
    }

    /**
     * PsByFlag finds flag and authenticates user.
     * @throws IOException If some problem inside
     */
    @Test
    public void flagIsFoundUserAuthenticated() throws IOException {
        MatcherAssert.assertThat(
            new PsByFlag(
                new PsByFlag.Pair(
                    "some-key", new PsFake(true)
                )
            ).enter(
                new RqFake("POST", "/?PsByFlag=some-key")
            ).next().urn(),
            Matchers.is("urn:test:1")
        );
    }

    /**
     * PsByFlag wraps response with authenticated user.
     * @throws IOException If some problem inside
     */
    @Test
    public void exitTest() throws IOException {
        final Response response = new RsWithStatus(
            new RsWithType(
                new RsWithBody("<html>This is test response</html>"),
                    "text/html"
            ),
            200
        );
        MatcherAssert.assertThat(
            new PsByFlag(
                ImmutableMap.of(
                    "key", (Pass) new PsFake(true)
                )
            ).exit(response, Mockito.mock(Identity.class)),
            Matchers.is(response)
        );
    }

    /**
     * Checks PsByFlag equals method.
     * @throws Exception If some problem inside
     */
    @Test
    public void equalsAndHashCodeEqualTest() throws Exception {
        EqualsVerifier.forClass(PsByFlag.class)
            .suppress(Warning.TRANSIENT_FIELDS)
            .verify();
    }
}
