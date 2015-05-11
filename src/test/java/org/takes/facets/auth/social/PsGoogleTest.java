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

/**
 * Test case for {@link PsGoogle}.
 *
 * <p>The class is immutable and thread-safe.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.16.3
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class PsGoogleTest {
    /**
     * PsGoogle login.
     * @throws IOException If some problem inside
     * @checkstyle MultipleStringLiteralsCheck (100 lines)
     * @throws IOException  If some problem inside
     */
    @Test
    public void logsIn() throws IOException {
        final Take take = new TkFork(
            new FkRegex(
                "/o/oauth2/token",
                // @checkstyle AnonInnerLengthCheck (1 line)
                new Take() {
                    @Override
                    public Response act(final Request req) throws IOException {
                        final Request greq = new RqGreedy(req);
                        PsGoogleTest.assertParam(greq, "client_id", "app");
                        PsGoogleTest.assertParam(
                            greq,
                            "redirect_uri",
                            "http://localhost/account"
                        );
                        PsGoogleTest.assertParam(greq, "client_secret", "key");
                        PsGoogleTest.assertParam(
                            greq,
                            "grant_type",
                            "authorization_code"
                        );
                        PsGoogleTest.assertParam(greq, "code", "code");
                        return new RsJSON(
                            Json.createObjectBuilder()
                                .add("access_token", "GoogleToken")
                                .add("expires_in", 1)
                                .add("token_type", "Bearer")
                                .build()
                        );
                    }
                }
            ),
            new FkRegex(
                "/oauth2/v1/userinfo",
                new Take() {
                    @Override
                    public Response act(final Request req) throws IOException {
                        MatcherAssert.assertThat(
                            new RqHref.Base(req).href().param("access_token")
                                .iterator().next(),
                            Matchers.containsString("GoogleToken")
                        );
                        return new RsJSON(
                            Json.createObjectBuilder()
                                .add("name", "octocat")
                                .add("id", "1")
                                .add(
                                    "picture",
                                    "https://google.com/img/octocat.gif"
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
                    final Identity identity = new PsGoogle(
                        "app",
                        "key",
                        "http://localhost/account",
                        home.toString(),
                        home.toString()
                    ).enter(new RqFake("GET", "?code=code")).next();
                    MatcherAssert.assertThat(
                        identity.urn(),
                        Matchers.equalTo("urn:google:1")
                    );
                    MatcherAssert.assertThat(
                        identity.properties().get("name"),
                        Matchers.equalTo("octocat")
                    );
                    MatcherAssert.assertThat(
                        identity.properties().get("picture"),
                        Matchers.equalTo("https://google.com/img/octocat.gif")
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
        final CharSequence param, final String value) throws IOException {
        MatcherAssert.assertThat(
            new RqForm.Smart(new RqForm.Base(req)).single(param),
            Matchers.equalTo(value)
        );
    }
}
