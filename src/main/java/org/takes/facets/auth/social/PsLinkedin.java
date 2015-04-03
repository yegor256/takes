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

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.Pass;
import org.takes.misc.Href;
import org.takes.rq.RqHref;

/**
 * Linkedin OAuth landing/callback page.
 *
 * <p>The class is immutable and thread-safe.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 */
@EqualsAndHashCode(of = { "app", "key" })
public final class PsLinkedin implements Pass {
    /**
     * Linkedin default OAUTH base url.
     */
    private static final String LINKEDIN_OAUTH = "https://www.linkedin.com";

    /**
     * Linkedin API base url.
     */
    private static final String LINKEDIN_API = "https://api.linkedin.com";

    /**
     * OAuth authorization code parameter.
     */
    private static final String CODE = "code";

    /**
     * App name.
     */
    private final transient String app;

    /**
     * Key.
     */
    private final transient String key;

    /**
        * Linkedin OAUTH base url.
    */
    private final transient String oauth;

    /**
     * Linkedin OAUTH base url.
     */
    private final transient String api;

    /**
     * Ctor.
     * @param lapp Linkedin app
     * @param lkey Linkedin key
     */
    public PsLinkedin(final String lapp, final String lkey) {
        this(LINKEDIN_OAUTH, LINKEDIN_API, lapp, lkey);
    }

    /**
     * Ctor.
     * @param loauth Linkedin oauth url
     * @param lapi Linkedin api url
     * @param lapp Linkedin app
     * @param lkey Linkedin key
     * @checkstyle ParameterNumberCheck (3 line)
     * @checkstyle LineLength (2 line)
     */
    PsLinkedin(final String loauth, final String lapi, final String lapp, final String lkey) {
        this.oauth = loauth;
        this.api = lapi;
        this.app = lapp;
        this.key = lkey;
    }

    @Override
    public Iterator<Identity> enter(final Request request)
        throws IOException {
        final Href href = new RqHref(request).href();
        final Iterator<String> code = href.param(PsLinkedin.CODE).iterator();
        if (!code.hasNext()) {
            throw new IllegalArgumentException("code is not provided");
        }
        return Collections.singleton(
            this.fetch(this.token(href.toString(), code.next()))
        ).iterator();
    }

    @Override
    public Response exit(final Response response,
        final Identity identity) {
        return response;
    }

    /**
     * Get user name from Linkedin, with the token provided.
     * @param token PsLinkedin access token
     * @return The user found in PsLinkedin
     * @throws IOException If fails
     */
    private Identity fetch(final String token) throws IOException {
        // @checkstyle LineLength (1 line)
        final String uri = new Href(this.api.concat("/v1/people/~:(id,first-name,last-name,picture-url)"))
            .with("format", "json")
            .with("oauth2_access_token", token)
            .toString();
        return PsLinkedin.parse(
            new JdkRequest(uri)
                .header("accept", "application/json")
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .as(JsonResponse.class).json().readObject()
        );
    }

    /**
     * Retrieve PsLinkedin access token.
     * @param home Home of this page
     * @param code PsLinkedin "authorization code"
     * @return The token
     * @throws IOException If failed
     */
    private String token(final String home, final String code)
        throws IOException {
        // @checkstyle LineLength (1 line)
        final String uri = new Href(this.oauth.concat("/uas/oauth2/accessToken"))
            .with("grant_type", "authorization_code")
            .with("client_id", this.app)
            .with("redirect_uri", home)
            .with("client_secret", this.key)
            .with(PsLinkedin.CODE, code)
            .toString();
        return new JdkRequest(uri)
            .method("POST")
            .header("Accept", "application/xml")
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json().readObject().getString("access_token");
    }

    /**
     * Make identity from JSON object.
     * @param json JSON received from Github
     * @return Identity found
     */
    private static Identity parse(final JsonObject json) {
        final ConcurrentMap<String, String> props =
            new ConcurrentHashMap<String, String>(json.size());
        props.put("first_name", json.getString("fname", "?"));
        props.put("last_name", json.getString("lname", ""));
        return new Identity.Simple(
            String.format("urn:linkedin:%d", json.getInt("id")), props
        );
    }
}
