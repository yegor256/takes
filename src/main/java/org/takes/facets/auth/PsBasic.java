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
package org.takes.facets.auth;

import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Pass that checks the user according RFC-2617.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Endrigo Antonini (teamed@endrigo.com.br)
 * @version $Id$
 * @since 0.20
 */
@EqualsAndHashCode(of = { "entry" })
public final class PsBasic implements Pass {

    /**
     * Authorization response HTTP head.
     */
    private static final String AUTH_HEAD = "Authorization: Basic";

    /**
     * Entry to validate user information.
     */
    private final transient PsBasic.Entry entry;

    /**
     * Ctor.
     * @param basic Entry
     */
    public PsBasic(final PsBasic.Entry basic) {
        this.entry = basic;
    }

    @Override
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public Opt<Identity> enter(final Request request) throws IOException {
        String authorization = "";
        String user = "";
        String pass = "";
        boolean found = false;
        Opt<Identity> identity = new Opt.Empty<Identity>();
        for (final String head : request.head()) {
            if (head.startsWith(AUTH_HEAD)) {
                authorization = head.split(AUTH_HEAD)[1].trim();
                final Decoder decoder = Base64.getDecoder();
                authorization = new String(decoder.decode(authorization));
                user = authorization.split(":")[0];
                pass = authorization.substring(user.length() + 1);
                found = true;
                break;
            }
        }
        if (found && this.entry.check(user, pass)) {
            final ConcurrentMap<String, String> props =
                new ConcurrentHashMap<String, String>(0);
            identity = new Opt.Single<Identity>(
                new Identity.Simple(
                    String.format("urn:basic:%s", user),
                    props
                )
            );
        }
        return identity;
    }

    @Override
    public Response exit(final Response response, final Identity identity)
        throws IOException {
        return response;
    }

    /**
     * Entry interface that is used to check if the received information is
     * valid.
     *
     * @author Endrigo Antonini (teamed@endrigo.com.br)
     * @version $Id$
     * @since 0.20
     */
    public interface Entry {

        /**
         * Check if is a valid user.
         * @param user User
         * @param pwd Password
         * @return If valid it return <code>true</code>.
         */
        boolean check(String user, String pwd);
    }

}
