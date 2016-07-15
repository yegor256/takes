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
     * PsLinkedin can login.
     * @throws IOException If some problem inside
     */
    @Test
    public void logins() throws IOException {
        final String code = RandomStringUtils.randomAlphanumeric(10);
        final String lapp = RandomStringUtils.randomAlphanumeric(10);
        final String lkey = RandomStringUtils.randomAlphanumeric(10);
        final String identifier = RandomStringUtils.randomAlphanumeric(10);
        final String tokenpath = "/uas/oauth2/accessToken";
        final String peoplepath = "/v1/people";
        final Take take = new TkFork(
            new FkRegex(tokenpath, new TokenTake(code, lapp, lkey)),
            new FkRegex(peoplepath, new PeopleTake(identifier))
        );
        new FtRemote(take).exec(
            new LinkedinScript(code, lapp, lkey, identifier)
        );
    }

    /**
     * Take that returns JSON with the authorization token.
     * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
     * @author Rui Castro (rui.castro@gmail.com)
     * @version $Id$
     * @since 0.33
     */
    private final class TokenTake implements Take {

        /**
         * Request path pattern for token endpoint.
         */
        private final String tokenpath;

        /**
         * Linkedin authorization code.
         */
        private final String code;

        /**
         * Linkedin app.
         */
        private final String lapp;

        /**
         * Linkedin key.
         */
        private final String lkey;

        /**
         * Ctor.
         * @param code Linkedin authorization code.
         * @param lapp Linkedin app.
         * @param lkey Linkedin key.
         */
        TokenTake(final String code, final String lapp, final String lkey) {
            this.tokenpath = "/uas/oauth2/accessToken";
            this.code = code;
            this.lapp = lapp;
            this.lkey = lkey;
        }

        @Override
        public Response act(final Request req) throws IOException {
            MatcherAssert.assertThat(
                new RqPrint(req).printBody(),
                Matchers.stringContainsInOrder(
                    Arrays.asList(
                        "grant_type=authorization_code",
                        String.format("client_id=%s", this.lapp),
                        "redirect_uri=",
                        String.format("client_secret=%s", this.lkey),
                        String.format("code=%s", this.code)
                    )
                )
            );
            MatcherAssert.assertThat(
                new RqHref.Base(req).href().toString(),
                Matchers.endsWith(this.tokenpath)
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
    private final class PeopleTake implements Take {

        /**
         * Linkedin user identifier.
         */
        private final String identifier;

        /**
         * Ctor.
         * @param identifier Linkedin user identifier.
         */
        PeopleTake(final String identifier) {
            this.identifier = identifier;
        }

        @Override
        public Response act(final Request req) throws IOException {
            return new RsJson(
                Json.createObjectBuilder()
                    .add("id", this.identifier)
                    .add("firstName", "Frodo")
                    .add("lastName", "Baggins")
                    .build()
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
    private final class LinkedinScript implements FtRemote.Script {

        /**
         * Linkedin authorization code.
         */
        private final String code;

        /**
         * Linkedin app.
         */
        private final String lapp;

        /**
         * Linkedin key.
         */
        private final String lkey;

        /**
         * Linkedin user identifier.
         */
        private final String identifier;

        /**
         * Ctor.
         * @param code Linkedin authorization code.
         * @param lapp Linkedin app.
         * @param lkey Linkedin key.
         * @param identifier Linkedin user identifier.
         * @checkstyle ParameterNumberCheck (4 lines)
         */
        LinkedinScript(final String code, final String lapp,
            final String lkey, final String identifier) {
            this.code = code;
            this.lapp = lapp;
            this.lkey = lkey;
            this.identifier = identifier;
        }

        @Override
        public void exec(final URI home) throws IOException {
            final Identity identity = new PsLinkedin(
                new Href(String.format("%s/uas/oauth2/accessToken", home)),
                new Href(String.format("%s/v1/people", home)),
                this.lapp,
                this.lkey
            ).enter(new RqFake("GET", String.format("?code=%s", this.code)))
                .get();
            MatcherAssert.assertThat(
                identity.urn(),
                CoreMatchers.equalTo(
                    String.format("urn:linkedin:%s", this.identifier)
                )
            );
            MatcherAssert.assertThat(
                identity.properties(),
                Matchers.allOf(
                    Matchers.hasEntry("firstName", "Frodo"),
                    Matchers.hasEntry("lastName", "Baggins")
                )
            );
        }
    }
}
