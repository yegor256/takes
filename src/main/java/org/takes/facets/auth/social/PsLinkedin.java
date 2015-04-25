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

import java.io.IOException;
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
 * @checkstyle LineLength (500 lines)
 */
@EqualsAndHashCode(of = { "json" })
public final class PsLinkedin implements Pass {
    /**
     * Social network member profile in json format.
     */
    private final transient MemberProfileJson json;

    /**
     * Ctor.
     * @param jsn Member profile json
     */
    public PsLinkedin(final MemberProfileJson jsn) {
        this.json = jsn;
    }

    @Override
    public Iterator<Identity> enter(final Request request)
        throws IOException {
        return Collections.singleton(
            PsLinkedin.parse(
                this.json.fetch(
                    new Href("https://www.linkedin.com/uas/oauth2/accessToken")
                        .with("grant_type", "authorization_code")
                        .toString(),
                    // @checkstyle LineLength (1 line)
                    "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,picture-url)",
                    new RqHref.Base(request).href()
                )
            )
        ).iterator();
    }

    @Override
    public Response exit(final Response response,
        final Identity identity) {
        return response;
    }

    /**
     * Make identity from JSON object.
     * @param json JSON received from LinkedIn
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
