/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Yegor Bugayenko
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

package org.takes.facets.auth.social;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import javax.json.Json;
import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.auth.Identity;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.FtRemote;
import org.takes.misc.Href;
import org.takes.rq.RqFake;
import org.takes.rq.RqHref;
import org.takes.rq.RqPrint;
import org.takes.rs.RsJson;

/**
 * Test case for {@link PsLinkedin}.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.16
 * @checkstyle MagicNumberCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
public final class PsLinkedinTest {

    /**
     * Field name for "First name".
     */
    private static final String FIRST_NAME = "firstName";

    /**
     * Test value for "First name".
     */
    private static final String FRODO = "Frodo";

    /**
     * Field name for "Last name".
     */
    private static final String LAST_NAME = "lastName";

    /**
     * Test value for "Last name".
     */
    private static final String BAGGINS = "Baggins";

    /**
     * Request path pattern for token endpoint.
     */
    private static final String TOKEN_PATTERN = "/uas/oauth2/accessToken";

    /**
     * Request path pattern for people endpoint.
     */
    private static final String PEOPLE_PATTERN = "/v1/people";

    /**
     * Linkedin authorization code.
     */
    private final String code = RandomStringUtils.randomAlphanumeric(10);

    /**
     * Linkedin app.
     */
    private final String lapp = RandomStringUtils.randomAlphanumeric(10);

    /**
     * Linkedin key.
     */
    private final String lkey = RandomStringUtils.randomAlphanumeric(10);

    /**
     * Linkedin user identifier.
     */
    private final String identifier = RandomStringUtils.randomAlphanumeric(10);

    /**
     * PsLinkedin can login.
     * @throws IOException If some problem inside
     */
    @Test
    public void logins() throws IOException {
        final Take take = new TkFork(
            new FkRegex(PsLinkedinTest.TOKEN_PATTERN, new TokenTake()),
            new FkRegex(PsLinkedinTest.PEOPLE_PATTERN, new PeopleTake())
        );
        new FtRemote(take).exec(new LinkedinScript());
    }

    /**
     * Take that returns JSON with the authorization token.
     * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
     * @author Rui Castro (rui.castro@gmail.com)
     * @version $Id$
     * @since 0.33
     */
    private class TokenTake implements Take {

        @Override
        public Response act(final Request req) throws IOException {
            MatcherAssert.assertThat(
                new RqPrint(req).printBody(),
                Matchers.stringContainsInOrder(
                    Arrays.asList(
                        "grant_type=authorization_code",
                        String.format(
                            "client_id=%s",
                            PsLinkedinTest.this.lapp
                        ),
                        "redirect_uri=",
                        String.format(
                            "client_secret=%s",
                            PsLinkedinTest.this.lkey
                        ),
                        String.format("code=%s", PsLinkedinTest.this.code)
                    )
                )
            );
            MatcherAssert.assertThat(
                new RqHref.Base(req).href().toString(),
                Matchers.endsWith(PsLinkedinTest.TOKEN_PATTERN)
            );
            return new RsJson(
                Json.createObjectBuilder()
                    .add(
                        "access_token",
                        RandomStringUtils.randomAlphanumeric(10)
                    ).build()
            );
        }
    }

    /**
     * Take that returns JSON with test user data.
     * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
     * @author Rui Castro (rui.castro@gmail.com)
     * @version $Id$
     * @since 0.33
     */
    private class PeopleTake implements Take {

        @Override
        public Response act(final Request req) throws IOException {
            return new RsJson(
                Json.createObjectBuilder()
                    .add(
                        "id",
                        PsLinkedinTest.this.identifier
                    ).add(
                        PsLinkedinTest.FIRST_NAME,
                        PsLinkedinTest.FRODO
                    ).add(
                        PsLinkedinTest.LAST_NAME,
                        PsLinkedinTest.BAGGINS
                    ).build()
            );
        }
    }

    /**
     * Script to test Linkedin authorization.
     * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
     * @author Rui Castro (rui.castro@gmail.com)
     * @version $Id$
     * @since 0.33
     */
    private class LinkedinScript implements FtRemote.Script {

        @Override
        public void exec(final URI home) throws IOException {
            final Identity identity = new PsLinkedin(
                new Href(
                    String.format("%s/uas/oauth2/accessToken", home)
                ),
                new Href(String.format("%s/v1/people/", home)),
                PsLinkedinTest.this.lapp,
                PsLinkedinTest.this.lkey
            ).enter(
                new RqFake(
                    "GET",
                    String.format("?code=%s", PsLinkedinTest.this.code)
                )
            ).get();
            MatcherAssert.assertThat(
                identity.urn(),
                CoreMatchers.equalTo(
                    String.format(
                        "urn:linkedin:%s",
                        PsLinkedinTest.this.identifier
                    )
                )
            );
            MatcherAssert.assertThat(
                identity.properties(),
                Matchers.allOf(
                    Matchers.hasEntry(
                        PsLinkedinTest.FIRST_NAME,
                        PsLinkedinTest.FRODO
                    ),
                    Matchers.hasEntry(
                        PsLinkedinTest.LAST_NAME,
                        PsLinkedinTest.BAGGINS
                    )
                )
            );
        }
    }
}
