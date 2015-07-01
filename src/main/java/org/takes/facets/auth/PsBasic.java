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
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.logging.Level;
import javax.xml.bind.DatatypeConverter;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.flash.RsFlash;
import org.takes.facets.forward.RsForward;
import org.takes.misc.Opt;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqHref;
import org.takes.rs.RsWithHeader;

/**
 * Pass that checks the user according RFC-2617.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Endrigo Antonini (teamed@endrigo.com.br)
 * @version $Id$
 * @since 0.20
 * @checkstyle ClassDataAbstractionCouplingCheck (250 lines)
 */
@EqualsAndHashCode(of = { "entry", "realm" })
public final class PsBasic implements Pass {

    /**
     * Authorization response HTTP head.
     */
    private static final String AUTH_HEAD = "Basic";

    /**
     * Entry to validate user information.
     */
    private final transient PsBasic.Entry entry;

    /**
     * Realm.
     */
    private final transient String realm;

    /**
     * Ctor.
     * @param rlm Realm
     * @param basic Entry
     */
    public PsBasic(final String rlm, final PsBasic.Entry basic) {
        this.realm = rlm;
        this.entry = basic;
    }

    @Override
    public Opt<Identity> enter(final Request request) throws IOException {
        final String decoded = new String(
            DatatypeConverter.parseBase64Binary(
                new RqHeaders.Smart(
                    new RqHeaders.Base(request)
                ).single("authorization").split(AUTH_HEAD)[1]
            )
        ).trim();
        final String user = decoded.split(":")[0];
        final Opt<Identity> identity = this.entry.enter(
            user,
            decoded.substring(user.length() + 1)
        );
        if (!identity.has()) {
            throw new RsForward(
                new RsWithHeader(
                    new RsFlash("access denied", Level.WARNING),
                    String.format(
                        "WWW-Authenticate: Basic ream=\"%s\"",
                        this.realm
                    )
                ),
                HttpURLConnection.HTTP_UNAUTHORIZED,
                new RqHref.Base(request).href()
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
     * Generate the identity urn.
     * @param user User
     * @return URN
     */
    private static String generateIdentityUrn(final String user) {
        return String.format("urn:basic:%s", user);
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
         * @return Identity.
         */
        Opt<Identity> enter(String user, String pwd);
    }

    /**
     * Fake implementation of {@link PsBasic.Entry}.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @author Endrigo Antonini (teamed@endrigo.com.br)
     * @version $Id$
     * @since 0.20
     *
     */
    public static final class Fake implements PsBasic.Entry {

        /**
         * Should we authenticate a user?
         */
        private final transient boolean condition;

        /**
         * Ctor.
         * @param cond Condition
         */
        public Fake(final boolean cond) {
            this.condition = cond;
        }

        @Override
        public Opt<Identity> enter(final String usr, final String pwd) {
            final Opt<Identity> user;
            if (this.condition) {
                user = new Opt.Single<Identity>(
                    new Identity.Simple(
                        PsBasic.generateIdentityUrn(usr)
                    )
                );
            } else {
                user = new Opt.Empty<Identity>();
            }
            return user;
        }
    }

    /**
     * Empty check.
     *
     * @author Endrigo Antonini (teamed@endrigo.com.br)
     * @version $Id$
     * @since 0.20
     */
    public static final class Empty implements PsBasic.Entry {

        @Override
        public Opt<Identity> enter(final String user, final String pwd) {
            return new Opt.Empty<Identity>();
        }
    }

    /**
     * Default implementation. Using list of user and pass.
     * @author lcozzani (lautaromail@gmail.com)
     * @version $Id$
     * @since 0.22
     */
    public static final class Default implements PsBasic.Entry {

        /**
         * Map of users and its passwords.
         */
        private final transient Map<String, String> users;

        /**
         * Ctor.
         * @param usrs Map of user and pass
         */
        public Default(final Map<String, String> usrs) {
            super();
            this.users = usrs;
        }

        @Override
        public Opt<Identity> enter(final String user, final String pwd) {
            Opt<Identity> valid = new Opt.Empty<Identity>();
            if (this.users.containsKey(user)
                && this.users.get(user).equals(pwd)) {
                valid = new Opt.Single<Identity>(
                    new Identity.Simple(PsBasic.generateIdentityUrn(user))
                );
            }
            return valid;
        }
    }
}
