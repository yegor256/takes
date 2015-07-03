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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
@SuppressWarnings("PMD.TooManyMethods")
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
                        String.format("urn:basic:%s", usr)
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
         * List of users and its passwords.
         */
        private final transient ConcurrentMap<String, PsBasic.User> users;

        /**
         * Ctor with list of "user,pass,urn".
         * @param list List of users.
         */
        public Default(final String ... list) {
            this(buildList(list));
        }

        /**
         * Ctor.
         * @param usrs Map of user and pass
         */
        public Default(final List<PsBasic.User> usrs) {
            this.users = Default.buildMap(usrs);
        }

        @Override
        public Opt<Identity> enter(final String user, final String pwd) {
            final Opt<Identity> valid;
            if (this.users.containsKey(user)
                && this.users.get(user).pass().equals(pwd)) {
                valid = new Opt.Single<Identity>(
                    new Identity.Simple(this.users.get(user).urn())
                );
            } else {
                valid = new Opt.Empty<Identity>();
            }
            return valid;
        }

        /**
         * Build list of users.
         * @param list List of users format "user,pass,urn"
         * @return List {@link List} of users
         */
        @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
        private static List<PsBasic.User> buildList(final String[] list) {
            final List<PsBasic.User> users =
                new ArrayList<PsBasic.User>(list.length);
            for (final String user : list) {
                final PsBasic.User usr = new PsBasic.User(user);
                users.add(usr);
            }
            return users;
        }

        /**
         * Build Map of users.
         * @param usrs List {@link List} of users.
         * @return Map {@link Map} of users.
         */
        private static ConcurrentMap<String, PsBasic.User> buildMap(
            final List<User> usrs
        ) {
            final ConcurrentMap<String, PsBasic.User> users =
                new ConcurrentHashMap<String, PsBasic.User>(usrs.size());
            for (final PsBasic.User user : usrs) {
                users.put(user.user(), user);
            }
            return users;
        }
    }

    /**
     * User for {@link PsBasic}.
     */
    public static final class User {

        /**
         * User.
         */
        private final transient String usr;

        /**
         * Password.
         */
        private final transient String psw;

        /**
         * URN.
         */
        private final transient String usrurn;

        /**
         * Ctor.
         * @param user User.
         * @param pass Pass.
         * @param urn Urn.
         */
        public User(final String user, final String pass, final String urn) {
            this.usr = user;
            this.psw = pass;
            this.usrurn = urn;
        }

        /**
         * Create usr with string.
         * @param str String with format "user,password,urn".
         */
        public User(final String str) {
            this(
                PsBasic.User.parse(str, 0),
                PsBasic.User.parse(str, 1),
                PsBasic.User.parse(str, 2)
            );
        }

        /**
         * User.
         * @return Returns the user.
         */
        public String user() {
            return this.usr;
        }

        /**
         * Pass.
         * @return Returns the pass.
         */
        public String pass() {
            return this.psw;
        }

        /**
         * Urn.
         * @return Returns the urn.
         */
        public String urn() {
            return this.usrurn;
        }

        /**
         * Parse user and return idx value.
         * @param str String with "user,pass,urn".
         * @param idx Index to return.
         * @return String of index.s
         */
        private static String parse(final String str, final int idx) {
            return str.split(",")[idx];
        }
    }
}
