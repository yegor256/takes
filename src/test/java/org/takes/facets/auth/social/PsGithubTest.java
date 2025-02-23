/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.social;

import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.auth.Identity;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqGreedy;
import org.takes.rq.form.RqFormBase;
import org.takes.rq.form.RqFormSmart;
import org.takes.rs.RsJson;
import org.takes.rs.xe.RsXembly;
import org.takes.rs.xe.XeDirectives;
import org.xembly.Directives;

/**
 * Test case for {@link org.takes.rq.RqMethod}.
 * @since 0.15.2
 */
final class PsGithubTest {

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

    @Test
    void failsOnNoAccessToken() {
        Assertions.assertThrows(
            AssertionError.class,
            () -> this.performLogin(PsGithubTest.directiveWithoutAccessToken())
        );
    }

    @Test
    void canLogin() throws Exception {
        this.performLogin(
            PsGithubTest.directiveWithoutAccessToken()
                .add(PsGithubTest.ACCESS_TOKEN)
                .set(PsGithubTest.GIT_HUB_TOKEN)
        );
    }

    /**
     * Performs the basic login.
     * @param directive The directive object.
     * @throws Exception If some problem inside.
     */
    private void performLogin(final Directives directive) throws Exception {
        final String app = "app";
        final String key = "key";
        final Take take = new TkFork(
            new FkRegex(
                "/login/oauth/access_token",
                (Take) req -> {
                    final Request greq = new RqGreedy(req);
                    final String code = "code";
                    PsGithubTest.assertParam(greq, code, code);
                    PsGithubTest.assertParam(greq, "client_id", app);
                    PsGithubTest.assertParam(greq, "client_secret", key);
                    return new RsXembly(
                        new XeDirectives(directive.toString())
                    );
                }
            ),
            new FkRegex(
                "/user",
                new TkFakeLogin()
            )
        );
        new FtRemote(take).exec(
            // @checkstyle AnonInnerLengthCheck (100 lines)
            home -> {
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
        final CharSequence param, final String value) throws IOException {
        MatcherAssert.assertThat(
            new RqFormSmart(new RqFormBase(req)).single(param),
            Matchers.equalTo(value)
        );
    }

    /**
     * An inner class for the Take implementation testing.
     * @since 0.15.2
     */
    private static final class TkFakeLogin implements Take {
        @Override
        public Response act(final Request req) throws IOException {
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
