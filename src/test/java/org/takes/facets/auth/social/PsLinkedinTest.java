/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.facets.auth.social;

import jakarta.json.Json;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
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
 * @since 0.16
 */
final class PsLinkedinTest {

    @Test
    void logins() throws Exception {
        final String tokenpath = "/uas/oauth2/accessToken";
        final String firstname = "firstName";
        final String lastname = "lastName";
        final String frodo = "Frodo";
        final String baggins = "Baggins";
        final String code = RandomStringUtils.randomAlphanumeric(10);
        final String lapp = RandomStringUtils.randomAlphanumeric(10);
        final String lkey = RandomStringUtils.randomAlphanumeric(10);
        final String identifier = RandomStringUtils.randomAlphanumeric(10);
        final Take take = new TkFork(
            new FkRegex(
                tokenpath,
                new TokenTake(code, lapp, lkey, tokenpath)
            ),
            new FkRegex(
                "/v1/people",
                new PeopleTake(identifier, firstname, lastname, frodo, baggins)
            )
        );
        new FtRemote(take).exec(
            new LinkedinScript(
                code, lapp, lkey, identifier,
                firstname, lastname, frodo, baggins
            )
        );
    }

    /**
     * Take that returns JSON with the authorization token.
     * @since 1.1
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
         * @param code Linkedin authorization code
         * @param lapp Linkedin app
         * @param lkey Linkedin key
         * @param tokenpath Request path for token endpoint
         * @checkstyle ParameterNumber (4 lines)
         */
        TokenTake(
            final String code, final String lapp, final String lkey,
            final String tokenpath
        ) {
            this.code = code;
            this.lapp = lapp;
            this.lkey = lkey;
            this.tokenpath = tokenpath;
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
     * @since 1.1
     */
    private final class PeopleTake implements Take {

        /**
         * Linkedin user identifier.
         */
        private final String identifier;

        /**
         * Field name for "First name".
         */
        private final String firstname;

        /**
         * Test value for "First name".
         */
        private final String frodo;

        /**
         * Field name for "Last name".
         */
        private final String lastname;

        /**
         * Test value for "Last name".
         */
        private final String baggins;

        /**
         * Ctor.
         * @param identifier Linkedin user identifier
         * @param firstname Field name for "First name"
         * @param lastname Field name for "Last name"
         * @param frodo Test value for "First name"
         * @param baggins Test value for "Last name"
         * @checkstyle ParameterNumberCheck (4 lines)
         */
        PeopleTake(
            final String identifier,
            final String firstname, final String lastname,
            final String frodo, final String baggins
        ) {
            this.identifier = identifier;
            this.firstname = firstname;
            this.lastname = lastname;
            this.frodo = frodo;
            this.baggins = baggins;
        }

        @Override
        public Response act(final Request req) throws IOException {
            return new RsJson(
                Json.createObjectBuilder()
                    .add("id", this.identifier)
                    .add(this.firstname, this.frodo)
                    .add(this.lastname, this.baggins)
                    .build()
            );
        }
    }

    /**
     * Script to test Linkedin authorization.
     * @since 1.1
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
         * Field name for "First name".
         */
        private final String firstname;

        /**
         * Test value for "First name".
         */
        private final String frodo;

        /**
         * Field name for "Last name".
         */
        private final String lastname;

        /**
         * Test value for "Last name".
         */
        private final String baggins;

        /**
         * Ctor.
         * @param code Linkedin authorization code
         * @param lapp Linkedin app
         * @param lkey Linkedin key
         * @param identifier Linkedin user identifier
         * @param firstname Field name for "First name"
         * @param lastname Field name for "Last name"
         * @param frodo Test value for "First name"
         * @param baggins Test value for "Last name"
         * @checkstyle ParameterNumberCheck (4 lines)
         */
        LinkedinScript(
            final String code, final String lapp,
            final String lkey, final String identifier,
            final String firstname, final String lastname,
            final String frodo, final String baggins
        ) {
            this.code = code;
            this.lapp = lapp;
            this.lkey = lkey;
            this.identifier = identifier;
            this.firstname = firstname;
            this.lastname = lastname;
            this.frodo = frodo;
            this.baggins = baggins;
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
                    Matchers.hasEntry(this.firstname, this.frodo),
                    Matchers.hasEntry(this.lastname, this.baggins)
                )
            );
        }
    }
}
