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
 * @since 0.11.3
 */
@EqualsAndHashCode(of = { "app", "key" })
public final class PsLinkedin implements Pass {
    /**
     * App name.
     */
    private final transient String app;

    /**
     * Key.
     */
    private final transient String key;

    /**
     * Ctor.
     * @param lapp Linkedin app
     * @param lkey Linkedin key
     */
    public PsLinkedin(final String lapp, final String lkey) {
        this.app = lapp;
        this.key = lkey;
    }

    @Override
    public Iterator<Identity> enter(final Request request)
        throws IOException {
        final Href href = new RqHref(request).href();
        // @checkstyle MultipleStringLiteralsCheck (1 line)
        final Iterator<String> code = href.param("code").iterator();
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
        final String uri = new Href("https://api.linkedin.com/v1/people/~:(id,first-name,last-name,picture-url)")
            .with("format", "json")
            .with("oauth2_access_token", token).toString();
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
        final String uri = new Href(
            "https://www.linkedin.com/uas/oauth2/accessToken"
        ).with("grant_type", "authorization_code")
            .with("client_id", this.app)
            .with("redirect_uri", home)
            .with("client_secret", this.key)
            .with("code", code)
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
        final String fname = "first_name";
        final String lname = "last_name";
        final String unknown = "?";
        final ConcurrentMap<String, String> props =
            new ConcurrentHashMap<String, String>(json.size());
        props.put(fname, json.getString(fname, unknown));
        props.put(lname, json.getString(lname, unknown));
        return new Identity.Simple(
            String.format("urn:linkedin:%d", json.getInt("id")), props
        );
    }
}
