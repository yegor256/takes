/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.social;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.DefaultWebRequestor;
import com.restfb.Version;
import com.restfb.WebRequestor;
import com.restfb.exception.FacebookException;
import com.restfb.types.User;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import lombok.EqualsAndHashCode;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.Pass;
import org.takes.misc.Href;
import org.takes.misc.Opt;
import org.takes.rq.RqHref;

/**
 * Facebook OAuth landing/callback page.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.5
 */
@EqualsAndHashCode(of = { "app", "key" })
public final class PsFacebook implements Pass {

    /**
     * Client id.
     */
    private static final String CLIENT_ID = "client_id";

    /**
     * Client secret.
     */
    private static final String CLIENT_SECRET = "client_secret";

    /**
     * Code.
     */
    private static final String CODE = "code";

    /**
     * Picture.
     */
    private static final String PICTURE = "picture";

    /**
     * Facebook access token URL.
     */
    private static final String ACCESS_TOKEN_URL =
        "https://graph.facebook.com/oauth/access_token";

    /**
     * Request for fetching app token.
     */
    private final com.jcabi.http.Request request;

    /**
     * Facebook login request handler.
     */
    private final WebRequestor requestor;

    /**
     * App name.
     */
    private final String app;

    /**
     * Key.
     */
    private final String key;

    /**
     * Ctor.
     * @param fapp Facebook app
     * @param fkey Facebook key
     */
    public PsFacebook(final String fapp, final String fkey) {
        this(
            new JdkRequest(
                new Href(PsFacebook.ACCESS_TOKEN_URL)
                    .with(PsFacebook.CLIENT_ID, fapp)
                    .with(PsFacebook.CLIENT_SECRET, fkey)
                    .toString()
            ),
            new DefaultWebRequestor(),
            fapp,
            fkey
        );
    }

    /**
     * Ctor with proper requestor for testing purposes.
     * @param frequest HTTP request for getting key
     * @param frequestor Facebook response
     * @param fapp Facebook app
     * @param fkey Facebook key
     * @checkstyle ParameterNumberCheck (3 lines)
     */
    PsFacebook(final com.jcabi.http.Request frequest,
        final WebRequestor frequestor, final String fapp, final String fkey) {
        this.request = frequest;
        this.requestor = frequestor;
        this.app = fapp;
        this.key = fkey;
    }

    @Override
    public Opt<Identity> enter(final Request trequest)
        throws IOException {
        final Href href = new RqHref.Base(trequest).href();
        final Iterator<String> code = href.param(PsFacebook.CODE).iterator();
        if (!code.hasNext()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                "code is not provided by Facebook"
            );
        }
        final User user = this.fetch(
            this.token(href.toString(), code.next())
        );
        final Map<String, String> props = new HashMap<>(0);
        props.put("name", user.getName());
        props.put(
            PsFacebook.PICTURE,
            new Href("https://graph.facebook.com/")
                .path(user.getId())
                .path(PsFacebook.PICTURE)
                .toString()
        );
        return new Opt.Single<>(
            new Identity.Simple(
                String.format("urn:facebook:%s", user.getId()),
                props
            )
        );
    }

    @Override
    public Response exit(final Response response,
        final Identity identity) {
        return response;
    }

    /**
     * Get the user name from Facebook with the provided token.
     * @param token Facebook access token
     * @return The user found in FB
     */
    private User fetch(final String token) {
        try {
            return new DefaultFacebookClient(
                token,
                this.requestor,
                new DefaultJsonMapper(),
                Version.LATEST
            ).fetchObject("me", User.class);
        } catch (final FacebookException ex) {
            throw new IllegalArgumentException(
                "Failed to fetch object from Facebook token",
                ex
            );
        }
    }

    /**
     * Retrieve Facebook access token.
     * @param home Home of this page
     * @param code Facebook "authorization code"
     * @return The token
     * @throws IOException If failed
     */
    private String token(final String home, final String code)
        throws IOException {
        final String response = this.request
            .uri()
            .set(
                URI.create(
                    new Href(PsFacebook.ACCESS_TOKEN_URL)
                        .with(PsFacebook.CLIENT_ID, this.app)
                        .with("redirect_uri", home)
                        .with(PsFacebook.CLIENT_SECRET, this.key)
                        .with(PsFacebook.CODE, code)
                        .toString()
                )
            )
            .back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK).body();
        final String[] sectors = response.split("&");
        for (final String sector : sectors) {
            final String[] pair = sector.split("=");
            if (pair.length != 2) {
                throw new IllegalArgumentException(
                    String.format("Invalid response: '%s'", response)
                );
            }
            if ("access_token".equals(pair[0])) {
                return pair[1];
            }
        }
        throw new IllegalArgumentException(
            String.format(
                "Access token not found in response: '%s'",
                response
            )
        );
    }

}
