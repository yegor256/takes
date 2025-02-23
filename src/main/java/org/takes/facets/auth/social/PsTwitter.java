/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.social;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import jakarta.json.JsonObject;
import jakarta.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.Pass;
import org.takes.misc.Href;
import org.takes.misc.Opt;

/**
 * Twitter OAuth landing/callback page.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.16
 */
@EqualsAndHashCode(of = {"app", "key"})
public final class PsTwitter implements Pass {

    /**
     * Access token.
     */
    private static final String ACCESS_TOKEN = "access_token";

    /**
     * Name.
     */
    private static final String NAME = "name";

    /**
     * URL for verifying user credentials.
     */
    private static final String VERIFY_URL =
        "https://api.twitter.com/1.1/account/verify_credentials.json";

    /**
     * App name.
     */
    private final String app;

    /**
     * Key.
     */
    private final String key;

    /**
     * Request for fetching app token.
     */
    private final com.jcabi.http.Request token;

    /**
     * Request for verifying user credentials.
     */
    private final com.jcabi.http.Request user;

    /**
     * Ctor.
     * @param name Twitter app
     * @param keys Twitter key
     */
    public PsTwitter(final String name, final String keys) {
        this(
            new JdkRequest(
                new Href("https://api.twitter.com/oauth2/token")
                    .with("grant_type", "client_credentials")
                    .toString()
            ),
            new JdkRequest(PsTwitter.VERIFY_URL), name, keys
        );
    }

    /**
     * Ctor with proper requestor for testing purposes.
     * @param tkn HTTP request for getting token
     * @param creds HTTP request for verifying credentials
     * @param name Facebook app
     * @param keys Facebook key
     * @checkstyle ParameterNumberCheck (3 lines)
     */
    PsTwitter(final com.jcabi.http.Request tkn,
        final com.jcabi.http.Request creds,
        final String name,
        final String keys) {
        this.token = tkn;
        this.user = creds;
        this.app = name;
        this.key = keys;
    }

    @Override
    public Opt<Identity> enter(final Request request)
        throws IOException {
        return new Opt.Single<>(this.identity(this.fetch()));
    }

    @Override
    public Response exit(final Response response, final Identity identity) {
        return response;
    }

    /**
     * Get user name from Twitter, with the token provided.
     * @param tkn Twitter access token
     * @return The user found in Twitter
     * @throws IOException If fails
     */
    private Identity identity(final String tkn) throws IOException {
        return parse(
            this.user
                .uri()
                .set(
                    URI.create(
                        new Href(PsTwitter.VERIFY_URL)
                            .with(PsTwitter.ACCESS_TOKEN, tkn)
                            .toString()
                    )
                )
                .back()
                .header("accept", "application/json")
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .as(JsonResponse.class)
                .json()
                .readObject()
        );
    }

    /**
     * Make identity from JSON object.
     * @param json JSON received from Twitter
     * @return Identity found
     */
    private static Identity parse(final JsonObject json) {
        final Map<String, String> props = new HashMap<>(json.size());
        props.put(PsTwitter.NAME, json.getString(PsTwitter.NAME));
        props.put("picture", json.getString("profile_image_url"));
        return new Identity.Simple(
            String.format("urn:twitter:%d", json.getInt("id")),
            props
        );
    }

    /**
     * Retrieve Twitter access token.
     * @return The Twitter access token
     * @throws IOException If failed
     */
    private String fetch() throws IOException {
        return this.token
            .method("POST")
            .header(
                "Content-Type",
                "application/x-www-form-urlencoded;charset=UTF-8"
            )
            .header(
                "Authorization",
                String.format(
                    "Basic %s", DatatypeConverter.printBase64Binary(
                        new UncheckedBytes(
                            new BytesOf(
                                String.format("%s:%s", this.app, this.key)
                            )
                        ).asBytes()
                    )
                )
            )
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json().readObject().getString(PsTwitter.ACCESS_TOKEN);
    }
}
