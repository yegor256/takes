/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.auth.Identity;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqGreedy;
import org.takes.rq.RqHref;
import org.takes.rq.form.RqFormBase;
import org.takes.rq.form.RqFormSmart;
import org.takes.rs.RsJson;
import org.takes.rs.xe.RsXembly;
import org.takes.rs.xe.XeDirectives;
import org.xembly.Directives;

/**
 * Test case for {@link org.takes.rq.RqMethod}.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.15.2
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class PsGithubTest {

    /**
     * GitHubToken.
     */
    private static final String GIT_HUB_TOKEN = "GitHubToken";

    /**
     * XPath access_token string.
     */
    private static final String ACCESS_TOKEN = "access_token";

    /**
     * XPath login string.
     */
    private static final String LOGIN = "login";

    /**
     * Octocat URL string.
     */
    private static final String OCTOCAT_GIF_URL =
        "https://github.com/img/octocat.gif";

    /**
     * XPath octocat string.
     */
    private static final String OCTOCAT = "octocat";

    /**
     * A Junit Exception test variable.
     */
    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    /**
     * PsGithub can fail on no access token.
     * @throws IOException If some problem inside.
     */
    @Test
    public void failsOnNoAccessToken() throws IOException {
        this.thrown.expect(AssertionError.class);
        this.performLogin(PsGithubTest.directiveWithoutAccessToken());
    }

    /**
     * PsGithub can login.
     * @throws IOException If some problem inside.
     */
    @Test
    public void canLogin() throws IOException {
        this.performLogin(
            PsGithubTest.directiveWithoutAccessToken()
                .add(PsGithubTest.ACCESS_TOKEN)
                .set(PsGithubTest.GIT_HUB_TOKEN)
        );
    }

    /**
     * Performs the basic login.
     * @param directive The directive object.
     * @throws IOException If some problem inside.
     */
    private void performLogin(final Directives directive) throws IOException {
        final String app = "app";
        final String key = "key";
        final Take take = new TkFork(
            new FkRegex(
                "/login/oauth/access_token",
                new Take() {
                    @Override
                    public Response act(final Request req) throws IOException {
                        final Request greq = new RqGreedy(req);
                        final String code = "code";
                        PsGithubTest.assertParam(greq, code, code);
                        PsGithubTest.assertParam(greq, "client_id", app);
                        PsGithubTest.assertParam(greq, "client_secret", key);
                        return new RsXembly(
                            new XeDirectives(directive.toString())
                        );
                    }
                }
            ),
            new FkRegex(
                "/user",
                new TkFakeLogin()
            )
        );
        new FtRemote(take).exec(
            // @checkstyle AnonInnerLengthCheck (100 lines)
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    final Identity identity = new PsGithub(
                        app,
                        key,
                        home.toString(),
                        home.toString()
                    ).enter(new RqFake("GET", "?code=code")).get();
                    MatcherAssert.assertThat(
                        identity.urn(),
                        Matchers.equalTo("urn:github:1")
                    );
                    MatcherAssert.assertThat(
                        identity.properties().get(PsGithubTest.LOGIN),
                        Matchers.equalTo(PsGithubTest.OCTOCAT)
                    );
                    MatcherAssert.assertThat(
                        identity.properties().get("avatar"),
                        Matchers.equalTo(PsGithubTest.OCTOCAT_GIF_URL)
                    );
                }
            }
        );
    }

    /**
     * Creates the basic directives, without access token.
     * @return A basic directive.
     */
    private static Directives directiveWithoutAccessToken() {
        return new Directives().add("OAuth")
            .add("token_type").set("bearer").up()
            .add("scope").set("repo,gist").up();
    }

    /**
     * Checks the parameter value for the expected value.
     * @param req Request
     * @param param Parameter name
     * @param value Parameter value
     * @throws IOException  If some problem inside
     */
    private static void assertParam(final Request req,
        final CharSequence param, final String value)  throws IOException {
        MatcherAssert.assertThat(
            new RqFormSmart(new RqFormBase(req)).single(param),
            Matchers.equalTo(value)
        );
    }

    /**
     * An inner class for the Take implementation testing.
     */
    private static final class TkFakeLogin implements Take {
        @Override
        public Response act(final Request req) throws IOException {
            MatcherAssert.assertThat(
                new RqHref.Base(req).href()
                    .param(PsGithubTest.ACCESS_TOKEN)
                    .iterator().next(),
                Matchers.containsString(PsGithubTest.GIT_HUB_TOKEN)
            );
            return new RsJson(
                Json.createObjectBuilder()
                    .add(PsGithubTest.LOGIN, PsGithubTest.OCTOCAT)
                    .add("id", 1)
                    .add(
                        "avatar_url",
                        PsGithubTest.OCTOCAT_GIF_URL
                    )
                    .build()
            );
        }
    }
}
