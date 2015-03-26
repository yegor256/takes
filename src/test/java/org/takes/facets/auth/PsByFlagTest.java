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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mock;
import org.takes.Response;
import org.takes.rq.RqFake;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWithType;

import java.io.IOException;

/**
 * Test case for {@link PsByFlag}.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.10
 */
public final class PsByFlagTest {
    @Mock
    Identity identity;

    /**
     * PsByFlag can skip if nothing found.
     *
     * @throws IOException If some problem inside
     */
    @Test
    public void skipsIfNothingFound() throws IOException {
        MatcherAssert.assertThat(
                new PsByFlag(
                        new PsByFlag.Pair(
                                "some-key", new PsFake(true)
                        )
                ).enter(new RqFake("GET", "/?PsByFlag=x")).hasNext(),
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
                new PsByFlag(
                        new PsByFlag.Pair(
                                "x", new PsFake(true)
                        )
                ).enter(new RqFake("GET", "/?PsByFlag=x")).next().urn(),
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
        Response response = new RsWithStatus(
                new RsWithType(
                        new RsWithBody("<html>This is test response</html>"),
                        "text/html"
                ),
                200
        );

        MatcherAssert.assertThat(
                new PsByFlag(
                        new PsByFlag.Pair(
                                "some-key", new PsFake(true)
                        )
                ).exit(response, identity),
                Matchers.is(response));
    }

    /**
     * Checks PsByFlag equality.
     *
     * @throws Exception If some problem inside
     */
    @Test
    public void equalsAndHashCodeEqualTest() throws Exception {
        MatcherAssert.assertThat(
                new PsByFlag(new PsByFlag.Pair("some-key", new PsFake(true))).equals(
                        new PsByFlag(new PsByFlag.Pair("some-key", new PsFake(true)))),
                Matchers.is(true));
    }

    /**
     * Checks PsByFlag inequality.
     *
     * @throws Exception If some problem inside
     */
    @Test
    public void equalsAndHashCodeNotEqualTest() throws Exception {
        MatcherAssert.assertThat(
                new PsByFlag(ImmutableMap.of("some-key", ((Pass) new PsFake(true)))).equals(
                        new PsByFlag(new PsByFlag.Pair("some-other-key", new PsFake(true)))),
                Matchers.is(false));
    }
}
