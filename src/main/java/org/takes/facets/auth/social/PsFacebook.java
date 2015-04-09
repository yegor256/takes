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
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.Pass;
import org.takes.misc.Href;
import org.takes.rq.RqHref;

/**
 * Facebook OAuth landing/callback page.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.5
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
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
    public Iterator<Identity> enter(final Request request)
        throws IOException {
        final Href href = new RqHref.Base(request).href();
        final Iterator<String> code = href.param("code").iterator();
        if (!code.hasNext()) {
            throw new IllegalArgumentException("code is not provided");
        }
        final User user = PsFacebook.fetch(
            this.token(href.toString(), code.next())
        );
        final ConcurrentMap<String, String> props =
            new ConcurrentHashMap<String, String>(0);
        props.put("name", user.getName());
        props.put(
            "picture",
            new Href("https://graph.facebook.com/")
                .path(user.getId())
                .path("picture")
                .toString()
        );
        return Collections.<Identity>singleton(
            new Identity.Simple(
                String.format("urn:facebook:%s", user.getId()),
                props
            )
        ).iterator();
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
     * Retrieve Facebook access token.
     * @param home Home of this page
     * @param code Facebook "authorization code"
     * @return The token
     * @throws IOException If failed
     */
    private String token(final String home, final String code)
        throws IOException {
        // @checkstyle LineLength (1 line)
        final String uri = new Href("https://graph.facebook.com/oauth/access_token")
            .with("client_id", this.app)
            .with("redirect_uri", home)
            .with("client_secret", this.key)
            .with("code", code)
            .toString();
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
