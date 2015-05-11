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
import org.takes.rs.RsJSON;

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
        final Take take = new TkFork(
            new FkRegex(
                "/uas/oauth2/accessToken",
                // @checkstyle AnonInnerLengthCheck (100 lines)
                new Take() {
                    @Override
                    public Response act(final Request req) throws IOException {
                        MatcherAssert.assertThat(
                            new RqPrint(req).printBody(),
                            Matchers.stringContainsInOrder(
                                Arrays.asList(
                                    "grant_type=authorization_code",
                                    String.format("client_id=%s", lapp),
                                    "redirect_uri=",
                                    String.format(
                                        "client_secret=%s",
                                        lkey
                                    ),
                                    String.format("code=%s", code)
                                )
                            )
                        );
                        MatcherAssert.assertThat(
                            new RqHref.Base(req).href().toString(),
                            Matchers.endsWith("/uas/oauth2/accessToken")
                        );
                        return new RsJSON(
                            Json.createObjectBuilder()
                                .add(
                                    "access_token",
                                    RandomStringUtils.randomAlphanumeric(10)
                                ).build()
                        );
                    }
                }
            ),
            new FkRegex(
                "/v1/people",
                new Take() {
                    @Override
                    public Response act(final Request req) throws IOException {
                        return new RsJSON(
                            Json.createObjectBuilder()
                                .add("id", identifier)
                                .add("firstName", "Frodo")
                                .add("lastName", "Baggins")
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
                    final Identity identity = new PsLinkedin(
                        new Href(
                            String.format(
                                "%s/uas/oauth2/accessToken",
                                home
                            )
                        ),
                        new Href(String.format("%s/v1/people/", home)),
                        lapp,
                        lkey
                    ).enter(
                        new RqFake("GET", String.format("?code=%s", code))
                    ).next();
                    MatcherAssert.assertThat(
                        identity.urn(),
                        CoreMatchers.equalTo(
                            String.format("urn:linkedin:%s", identifier)
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
        );
    }
}
