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
import com.jcabi.http.response.XmlResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.Pass;
import org.takes.rq.RqQuery;
import org.takes.rq.RqURI;

/**
 * Github OAuth landing/callback page.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "app", "key" })
public final class PsGithub implements Pass {

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
     * @param gapp Github app
     * @param gkey Github key
     */
    public PsGithub(final String gapp, final String gkey) {
        this.app = gapp;
        this.key = gkey;
    }

    @Override
    public Identity enter(final Request request) throws IOException {
        final List<String> code = new RqQuery(request).param("code");
        if (code.isEmpty()) {
            throw new IllegalArgumentException("code is not provided");
        }
        return PsGithub.fetch(
            this.token(new RqURI(request).uri(), code.get(0))
        );
    }

    @Override
    public Response exit(final Response response,
        final Identity identity) {
        return response;
    }

    /**
     * Get user name from Github, with the token provided.
     * @param token Github access token
     * @return The user found in Github
     * @throws IOException If fails
     */
    private static Identity fetch(final String token) throws IOException {
        final String uri = String.format(
            "https://api.github.com/user?access_token=%s",
            URLEncoder.encode(token, Charset.defaultCharset().name())
        );
        return PsGithub.parse(
            new JdkRequest(uri)
                .header("accept", "application/json")
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .as(JsonResponse.class).json().readObject()
        );
    }

    /**
     * Retrieve Github access token.
     * @param home Home of this page
     * @param code Github "authorization code"
     * @return The token
     * @throws IOException If failed
     */
    private String token(final URI home, final String code) throws IOException {
        final String uri = String.format(
            // @checkstyle LineLength (1 line)
            "https://github.com/login/oauth/access_token?client_id=%s&redirect_uri=%s&client_secret=%s&code=%s",
            URLEncoder.encode(this.app, Charset.defaultCharset().name()),
            URLEncoder.encode(home.toString(), Charset.defaultCharset().name()),
            URLEncoder.encode(this.key, Charset.defaultCharset().name()),
            URLEncoder.encode(code, Charset.defaultCharset().name())
        );
        return new JdkRequest(uri)
            .method("POST")
            .header("Accept", "application/xml")
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(XmlResponse.class)
            .xml().xpath("/OAuth/access_token/text()").get(0);
    }

    /**
     * Make identity from JSON object.
     * @param json JSON received from Github
     * @return Identity found
     */
    private static Identity parse(final JsonObject json) {
        return new Identity() {
            @Override
            public String urn() {
                return String.format("urn:github:%d", json.getInt("id"));
            }
            @Override
            public Map<String, String> properties() {
                final ConcurrentMap<String, String> props =
                    new ConcurrentHashMap<String, String>(json.size());
                // @checkstyle MultipleStringLiteralsCheck (1 line)
                props.put("login", json.getString("login", "?"));
                props.put("avatar", json.getString("avatar_url", "#"));
                return props;
            }
        };
    }

}
