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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.takes.Response;
import org.takes.rq.RqFake;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWithType;

/**
 * Test case for {@link PsByFlag}.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.10
 */
public final class PsByFlagTest {
    /**
     * Testable PsByFlag object.
     */
    private static PsByFlag psbyflag;
    /**
     * Key.
     */
    private static final String KEY = "some-key";
    /**
     * HTTP request method.
     */
    private static final String METHOD = "GET";

    /**
     * Mocked Identity object used to test exit method.
     */
    @Mock
    private static Identity identity;

    /**
     * Test set up.
     *
     * @throws Exception If some problem inside
     */
    @Before
    public void setUp() throws Exception {
        psbyflag = new PsByFlag(
            new PsByFlag.Pair(
                KEY, new PsFake(true)
            )
        );
    }

    /**
     * PsByFlag can skip if nothing found.
     *
     * @throws IOException If some problem inside
     */
    @Test
    public void skipsIfNothingFound() throws IOException {
        MatcherAssert.assertThat(
            psbyflag.enter(
                new RqFake(METHOD, "/?PsByFlag=x")
            ).hasNext(),
            Matchers.is(false)
        );
    }

    /**
     * PsByFlag finds flag and authenticates user.
     *
     * @throws IOException If some problem inside
     */
    @Test
    public void flagIsFoundUserAuthenticated() throws IOException {
        MatcherAssert.assertThat(
            psbyflag.enter(
                new RqFake(METHOD, "/?PsByFlag=some-key")
            ).next().urn(),
            Matchers.is("urn:test:1")
        );
    }

    /**
     * PsByFlag wraps response with authenticated user.
     *
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
                new PsByFlag.Pair(
                    KEY, new PsFake(true)
                )
            ).exit(response, identity),
            Matchers.is(response)
        );
    }

    /**
     * Checks PsByFlag equality.
     *
     * @throws Exception If some problem inside
     */
    @Test
    public void equalsAndHashCodeEqualTest() throws Exception {
        MatcherAssert.assertThat(
            psbyflag.equals(
                new PsByFlag(
                    new PsByFlag.Pair(
                        KEY, new PsFake(true)
                    )
                )
            ),
            Matchers.is(true)
        );
    }

    /**
     * Checks PsByFlag inequality.
     *
     * @throws Exception If some problem inside
     */
    @Test
    public void equalsAndHashCodeNotEqualTest() throws Exception {
        MatcherAssert.assertThat(
            psbyflag.equals(
                new PsByFlag(
                    ImmutableMap.of(
                        "some-other-key", (Pass) new PsFake(true)
                    )
                )
            ),
            Matchers.is(false)
        );
    }
}
