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
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.json.JsonObject;
import javax.xml.bind.DatatypeConverter;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.Pass;
import org.takes.misc.Href;

/**
 * Twitter OAuth landing/callback page.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Prasath Premkumar (popprem@gmail.com)
 * @version $Id$
 * @since
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@EqualsAndHashCode(of = { "app", "key" })
public final class PsTwitter implements Pass {

    /**
     * URL for verifying user credentials.
     */
    private static final String VERIFY_URL =
        "https://api.twitter.com/1.1/account/verify_credentials.json";

    /**
     * App name.
     */
    private final transient String app;

    /**
     * Key.
     */
    private final transient String key;

    /**
     * Request for fetching app token.
     */
    private final transient com.jcabi.http.Request trequest;

    /**
     * Request for verifying user credentials.
     */
    private final transient com.jcabi.http.Request vcrequest;

    /**
     * Ctor.
     * @param tapp Twitter app
     * @param tkey Twitter key
     */
    public PsTwitter(final String tapp, final String tkey) {
        this(
            new JdkRequest(
                new Href("https://api.twitter.com/oauth2/token")
                    .with("grant_type", "client_credentials")
                    .toString()
            ),
            new JdkRequest(VERIFY_URL),
            tapp,
            tkey
        );
    }

    /**
     * Ctor with proper requestor for testing purposes.
     * @param ttrequest HTTP request for getting token
     * @param tvcrequest HTTP request for verifying credentials
     * @param tapp Facebook app
     * @param tkey Facebook key
     * @checkstyle ParameterNumberCheck (3 lines)
     */
    PsTwitter(final com.jcabi.http.Request ttrequest,
        final com.jcabi.http.Request tvcrequest,
        final String tapp,
        final String tkey) {
        this.trequest = ttrequest;
        this.vcrequest = tvcrequest;
        this.app = tapp;
        this.key = tkey;
    }

    @Override
    public Iterator<Identity> enter(final Request request) throws IOException {
        return Collections.singleton(
            this.fetch(this.token())
        ).iterator();
    }

    @Override
    public Response exit(final Response response, final Identity identity) {
        return response;
    }

    /**
     * Get user name from Twitter, with the token provided.
     * @param token Twitter access token
     * @return The user found in Twitter
     * @throws IOException If fails
     */
    private Identity fetch(final String token) throws IOException {
        final JsonObject response = this.vcrequest
            .uri()
            .set(
                URI.create(
                    new Href(VERIFY_URL)
                        .with("access_token", token)
                        .toString()
                )
            )
            .back()
            .header("accept", "application/json")
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class).json().readObject();
        final ConcurrentMap<String, String> props =
            new ConcurrentHashMap<String, String>(response.size());
        props.put("name", response.getString("name"));
        props.put("picture", response.getString("profile_image_url"));
        return new Identity.Simple(
            String.format("urn:twitter:%d", response.getInt("id")), props
        );
    }

    /**
     * Retrieve Twitter access token.
     * @return The token
     * @throws IOException If failed
     */
    private String token() throws IOException {
        return this.trequest
            .method("POST")
            .header(
                "Content-Type",
                "application/x-www-form-urlencoded;charset=UTF-8"
            )
            .header(
                "Authorization",
                String.format(
                    "Basic %s", DatatypeConverter.printBase64Binary(
                        String.format("%s:%s", this.app, this.key).getBytes()
                    )
                )
            )
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json().readObject().getString("access_token");
    }
}
