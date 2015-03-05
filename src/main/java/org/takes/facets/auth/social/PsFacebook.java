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
import com.jcabi.http.response.RestResponse;
import com.restfb.DefaultFacebookClient;
import com.restfb.exception.FacebookException;
import com.restfb.types.User;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.Pass;
import org.takes.rq.RqQuery;
import org.takes.rq.RqURI;

/**
 * Facebook OAuth landing/callback page.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.5
 */
@EqualsAndHashCode(of = { "app", "key" })
public final class PsFacebook implements Pass {

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
     * @param fapp Facebook app
     * @param fkey Facebook key
     */
    public PsFacebook(final String fapp, final String fkey) {
        this.app = fapp;
        this.key = fkey;
    }

    @Override
    public Identity enter(final Request request) throws IOException {
        final List<String> code = new RqQuery(request).param("code");
        if (code.isEmpty()) {
            throw new IllegalArgumentException("code is not provided");
        }
        final User user = PsFacebook.fetch(
            this.token(new RqURI(request).uri(), code.get(0))
        );
        return new Identity() {
            @Override
            public String urn() {
                return String.format("urn:facebook:%s", user.getId());
            }
            @Override
            public Map<String, String> properties() {
                final ConcurrentMap<String, String> props =
                    new ConcurrentHashMap<String, String>(0);
                props.put("name", user.getName());
                props.put(
                    "picture",
                    String.format(
                        "https://graph.facebook.com/%s/picture",
                        user.getId()
                    )
                );
                return props;
            }
        };
    }

    @Override
    public Response exit(final Response response,
        final Identity identity) {
        return response;
    }

    /**
     * Get user name from Facebook, but the code provided.
     * @param token Facebook access token
     * @return The user found in FB
     */
    private static User fetch(final String token) {
        try {
            return new DefaultFacebookClient(token).fetchObject(
                "me", User.class
            );
        } catch (final FacebookException ex) {
            throw new IllegalArgumentException(ex);
        }
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
            "https://graph.facebook.com/oauth/access_token?client_id=%s&redirect_uri=%sclient_secret=%s&code=%s",
            URLEncoder.encode(this.app, Charset.defaultCharset().name()),
            URLEncoder.encode(home.toString(), Charset.defaultCharset().name()),
            URLEncoder.encode(this.key, Charset.defaultCharset().name()),
            URLEncoder.encode(code, Charset.defaultCharset().name())
        );
        final String response = new JdkRequest(uri)
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .body();
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
