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
package org.takes.facets.auth.social;

import java.io.IOException;
import java.net.URI;
import javax.json.Json;
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
import org.takes.rq.RqFake;
import org.takes.rq.RqForm;
import org.takes.rq.RqGreedy;
import org.takes.rq.RqHref;
import org.takes.rs.RsJSON;
import org.takes.rs.xe.RsXembly;
import org.takes.rs.xe.XeDirectives;
import org.xembly.Directives;

/**
 * Test case for {@link org.takes.rq.RqMethod}.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @since 0.15.2
 */
public final class PsGithubTest {
    /**
     * PsGithub can login.
     * @throws IOException If some problem inside
     * @checkstyle MultipleStringLiteralsCheck (100 lines)s
     */
    @Test
    public void canLogin() throws IOException {
        final Take take = new TkFork(
            new FkRegex(
                "/login/oauth/access_token",
                new Take() {
                    @Override
                    public Response act(final Request req) throws IOException {
                        final Request greq = new RqGreedy(req);
                        PsGithubTest.assertParam(greq, "code", "code");
                        PsGithubTest.assertParam(greq, "client_id", "app");
                        PsGithubTest.assertParam(greq, "client_secret", "key");
                        return new RsXembly(
                            new XeDirectives(
                                new Directives().add("OAuth")
                                    .add("token_type").set("bearer").up()
                                    .add("scope").set("repo,gist").up()
                                    .add("access_token").set("GitHubToken")
                                    .toString()
                            )
                        );
                    }
                }
            ),
            new FkRegex(
                "/user",
                new Take() {
                    @Override
                    public Response act(final Request req) throws IOException {
                        MatcherAssert.assertThat(
                            new RqHref.Base(req).href().param("access_token")
                                .iterator().next(),
                            Matchers.containsString("GitHubToken")
                        );
                        return new RsJSON(
                            Json.createObjectBuilder()
                                .add("login", "octocat")
                                .add("id", 1)
                                .add(
                                    "avatar_url",
                                    "https://github.com/img/octocat.gif"
                                )
                                .build()
                        );
                    }
                }
            )
        );
        new FtRemote(take).exec(
            // @checkstyle AnonInnerLengthCheck (100 lines)
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    final Identity identity = new PsGithub(
                        "app",
                        "key",
                        home.toString(),
                        home.toString()
                    ).enter(new RqFake("GET", "?code=code")).next();
                    MatcherAssert.assertThat(
                        identity.urn(),
                        Matchers.equalTo("urn:github:1")
                    );
                    MatcherAssert.assertThat(
                        identity.properties().get("login"),
                        Matchers.equalTo("octocat")
                    );
                    MatcherAssert.assertThat(
                        identity.properties().get("avatar"),
                        Matchers.equalTo("https://github.com/img/octocat.gif")
                    );
                }
            }
        );
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
            new RqForm.Smart(new RqForm.Base(req)).single(param),
            Matchers.equalTo(value)
        );
    }
}
