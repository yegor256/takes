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
import java.util.Iterator;
import javax.json.JsonObject;
import org.takes.misc.Href;

/**
 * LinkedIn member profile in json format.
 *
 * <p>The class is immutable and thread-safe.
 * @author Igor Khvostenkov (ikhvostenkov@gmail.com)
 * @version $Id$
 * @since 0.14.4
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
public final class LinkedinProfileJson implements MemberProfileJson {
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
     * @param sapp Social network app
     * @param skey Social network key
     */
    public LinkedinProfileJson(final String sapp, final String skey) {
        this.app = sapp;
        this.key = skey;
    }

    @Override
    public JsonObject fetch(final String tokenurl, final String socialurl,
                            final Href home) throws IOException {
        // @checkstyle MultipleStringLiteralsCheck (1 line)
        final Iterator<String> code = home.param("code").iterator();
        if (!code.hasNext()) {
            throw new IllegalArgumentException("code is not provided");
        }
        return new JdkRequest(new Href(socialurl).with("format", "json")
            .with(
                "oauth2_access_token",
                this.token(tokenurl, home.toString(), code.next())
            ).toString()
        ).header("accept", "application/json")
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class).json().readObject();
    }

    /**
     * Retrieves social network access token.
     * @param tokenurl The token URL
     * @param home Home of this page
     * @param code Social network "authorization code"
     * @return The token
     * @throws IOException If failed
     */
    private String token(final String tokenurl, final String home,
                         final String code)
        throws IOException {
        final String uri = new Href(tokenurl)
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

}
