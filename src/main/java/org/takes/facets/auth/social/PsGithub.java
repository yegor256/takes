/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.social;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.XmlResponse;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import lombok.EqualsAndHashCode;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.Pass;
import org.takes.misc.Href;
import org.takes.misc.Opt;
import org.takes.rq.RqHref;

/**
 * Github OAuth landing/callback page.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(of = { "app", "key" })
public final class PsGithub implements Pass {

    /**
     * Access token.
     */
    private static final String ACCESS_TOKEN = "access_token";

    /**
     * Code.
     */
    private static final String CODE = "code";

    /**
     * Login.
     */
    private static final String LOGIN = "login";

    /**
     * App name.
     */
    private final String app;

    /**
     * Key.
     */
    private final String key;

    /**
     * GitHub OAuth url.
     */
    private final String github;

    /**
     * GitHub API url.
     */
    private final String api;

    /**
     * Ctor.
     * @param gapp Github app
     * @param gkey Github key
     */
    public PsGithub(final String gapp, final String gkey) {
        this(gapp, gkey, "https://github.com", "https://api.github.com");
    }

    /**
     * Ctor.
     * @param gapp Github app
     * @param gkey Github key
     * @param gurl Github OAuth server
     * @param aurl Github API server
     */
    PsGithub(final String gapp, final String gkey,
        final String gurl, final String aurl) {
        this.app = gapp;
        this.key = gkey;
        this.github = gurl;
        this.api = aurl;
    }

    @Override
    public Opt<Identity> enter(final Request request) throws IOException {
        final Href href = new RqHref.Base(request).href();
        final Iterator<String> code = href.param(PsGithub.CODE).iterator();
        if (!code.hasNext()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                "'code' is not provided by Github"
            );
        }
        return new Opt.Single<>(
            this.fetch(this.token(href.toString(), code.next()))
        );
    }

    @Override
    public Response exit(final Response response, final Identity identity) {
        return response;
    }

    private Identity fetch(final String token) throws IOException {
        return PsGithub.parse(
            new JdkRequest(new Href(this.api).path("user").toString())
                .header("accept", "application/json")
                .header("Authorization", PsGithub.authorization(token))
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .as(JsonResponse.class)
                .json()
                .readObject()
        );
    }

    private String token(final String home, final String code)
        throws IOException {
        return new JdkRequest(
            new Href(this.github)
                .path(PsGithub.LOGIN).path("oauth").path(PsGithub.ACCESS_TOKEN)
                .toString()
        ).method("POST")
            .header("Accept", "application/xml")
            .body()
            .formParam("client_id", this.app)
            .formParam("redirect_uri", home)
            .formParam("client_secret", this.key)
            .formParam(PsGithub.CODE, code)
            .back()
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(XmlResponse.class)
            .assertXPath("/OAuth/access_token")
            .xml()
            .xpath("/OAuth/access_token/text()")
            .get(0);
    }

    private static String authorization(final String token) {
        return new UncheckedText(
            new FormattedText("token %s", token)
        ).asString();
    }

    private static Identity parse(final JsonObject json) {
        final Map<String, String> props = new HashMap<>(json.size());
        props.put(PsGithub.LOGIN, json.getString(PsGithub.LOGIN, "unknown"));
        props.put("avatar", json.getString("avatar_url", "#"));
        return new Identity.Simple(
            new UncheckedText(
                new FormattedText(
                    "urn:github:%d", json.getInt("id")
                )
            ).asString(), props
        );
    }
}
