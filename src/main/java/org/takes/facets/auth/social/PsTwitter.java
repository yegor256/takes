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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import org.apache.commons.codec.binary.Base64;
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
     * UTF-8 encoding.
     */
    private static final String ENCODING = "UTF-8";

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
     * @param gapp Twitter app
     * @param gkey Twitter key
     */
    public PsTwitter(final String gapp, final String gkey) {
        this.app = gapp;
        this.key = gkey;
    }

    @Override
    public Iterator<Identity> enter(final Request request)
        throws IOException {
        return Collections.singleton(
            PsTwitter.fetch(this.token())
        ).iterator();
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
        final String uri = new Href(
                "https://api.twitter.com/1.1/account/verify_credentials.json"
        )
            .with("access_token", token)
            .toString();
        return PsTwitter.parse(
            new JdkRequest(uri)
                .header("accept", "application/json")
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .as(JsonResponse.class).json().readObject()
        );
    }

    /**
     * Retrieve Github access token.
     * @return The token
     * @throws IOException If failed
     */
    private String token()
        throws IOException {
        final String uri = new Href("https://api.twitter.com/oauth2/token")
            .with("grant_type", "client_credentials")
            .toString();
        return new JdkRequest(uri)
            .method("POST")
                .header(
                        "Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8"
            )
                .header(
                        "Authorization",
                        "Basic ".concat(
                                PsTwitter.prepareAuthKey(
                                this.app, this.key
                    )
                )
            )
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
        props.put("name", json.getString("name"));
        props.put("picture", json.getString("profile_image_url"));
        return new Identity.Simple(
            String.format("urn:twitter:%d", json.getInt("id")), props
        );
    }

    /**
     * Encode consumerKey and consumerSecret and prepare authorization key.
     * @param consumerkey Twitter consumerKey
     * @param consumersecret Twitter consumerSecret
     * @return String authorization key
     */
    private static String prepareAuthKey(final String consumerkey,
            final String consumersecret) {
        try {
            final String authkey = URLEncoder.encode(consumerkey, ENCODING)
                    + ":" + URLEncoder.encode(consumersecret, ENCODING);
            final byte[] encodedBytes = Base64.encodeBase64(
                    authkey.getBytes()
            );
            return new String(encodedBytes);
        } catch (final UnsupportedEncodingException  ex) {
            throw new EncodingException(ex);
        }
    }
}
