/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.social;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
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
 * Google OAuth landing/callback page.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.9
 */
@EqualsAndHashCode(of = { "app", "key", "redir" })
public final class PsGoogle implements Pass {

    /**
     * Error.
     */
    private static final String ERROR = "error";

    /**
     * Picture.
     */
    private static final String PICTURE = "picture";

    /**
     * Display name.
     */
    private static final String DISPLAY_NAME = "displayName";

    /**
     * Access token.
     */
    private static final String ACCESS_TOKEN = "access_token";

    /**
     * Name.
     */
    private static final String NAME = "name";

    /**
     * Code.
     */
    private static final String CODE = "code";

    /**
     * App name.
     */
    private final String app;

    /**
     * Key.
     */
    private final String key;

    /**
     * Redirect URI.
     */
    private final String redir;

    /**
     * Google OAuth url.
     */
    private final String gauth;

    /**
     * Google API url.
     */
    private final String gapi;

    /**
     * Ctor.
     * @param gapp Google app
     * @param gkey Google key
     * @param uri Redirect URI (exactly as registered in Google console)
     */
    public PsGoogle(final String gapp, final String gkey,
        final String uri) {
        this(
            gapp,
            gkey,
            uri,
            "https://accounts.google.com",
            "https://www.googleapis.com"
        );
    }

    /**
     * Ctor.
     * @param gapp Google app
     * @param gkey Google key
     * @param uri Redirect URI (exactly as registered in Google console)
     * @param gurl Google OAuth url
     * @param aurl Google API url
     * @checkstyle ParameterNumberCheck (2 lines)
     */
    PsGoogle(final String gapp, final String gkey,
        final String uri, final String gurl, final String aurl) {
        this.app = gapp;
        this.key = gkey;
        this.redir = uri;
        this.gauth = gurl;
        this.gapi = aurl;
    }

    @Override
    public Opt<Identity> enter(final Request request)
        throws IOException {
        final Href href = new RqHref.Base(request).href();
        final Iterator<String> code = href.param(PsGoogle.CODE).iterator();
        if (!code.hasNext()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                "code is not provided by Google, probably some mistake"
            );
        }
        return new Opt.Single<>(this.fetch(this.token(code.next())));
    }

    @Override
    public Response exit(final Response response,
        final Identity identity) {
        return response;
    }

    /**
     * Get the user name from Google with the provided token.
     * @param token Google access token
     * @return The user found in Google
     * @throws IOException If fails
     */
    private Identity fetch(final String token) throws IOException {
        final String uri = new Href(this.gapi).path("plus").path("v1")
            .path("people")
            .path("me")
            .with(PsGoogle.ACCESS_TOKEN, token)
            .toString();
        final JsonObject json = new JdkRequest(uri).fetch()
            .as(JsonResponse.class).json()
            .readObject();
        if (json.containsKey(PsGoogle.ERROR)) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format(
                    "could not retrieve id from Google, possible cause: %s.",
                    json.getJsonObject(PsGoogle.ERROR).get("message")
                )
            );
        }
        return PsGoogle.parse(json);
    }

    /**
     * Retrieve Google access token.
     * @param code Google "authorization code"
     * @return The token
     * @throws IOException If failed
     */
    private String token(final String code) throws IOException {
        return new JdkRequest(
            new Href(this.gauth).path("o").path("oauth2").path("token")
                .toString()
        ).body()
            .formParam("client_id", this.app)
            .formParam("redirect_uri", this.redir)
            .formParam("client_secret", this.key)
            .formParam("grant_type", "authorization_code")
            .formParam(PsGoogle.CODE, code)
            .back()
            .header("Content-Type", "application/x-www-form-urlencoded")
            .method(com.jcabi.http.Request.POST)
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class).json()
            .readObject()
            .getString(PsGoogle.ACCESS_TOKEN);
    }

    /**
     * Make identity from JSON object.
     * @param json JSON received from Google
     * @return Identity found
     */
    private static Identity parse(final JsonObject json) {
        final Map<String, String> props = new HashMap<>(json.size());
        final Opt<JsonObject> image = new Opt.Single<>(
            json.getJsonObject("image")
        );
        if (image.has()) {
            props.put(PsGoogle.PICTURE, image.get().getString("url", "#"));
        } else {
            props.put(PsGoogle.PICTURE, "#");
        }
        props.put(
            PsGoogle.NAME, json.getString(PsGoogle.DISPLAY_NAME, "unknown")
        );
        return new Identity.Simple(
            String.format("urn:google:%s", json.getString("id")), props
        );
    }

}
