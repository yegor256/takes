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
import java.util.HashMap;
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
     * Default entry.
     *
     * @author Georgy Vlasov (wlasowegor@gmail.com)
     * @version $Id$
     * @since 0.22
     */
    public static final class Default implements PsBasic.Entry {

        /**
         * How keys in
         * {@link org.takes.facets.auth.PsBasic.Default#usernames} are
         * formatted.
         */
        public static final String KEY_FORMAT = "%s %s";

        /**
         * Map from login/password pairs to URNs.
         */
        private final transient Map<String, String> usernames;

        /**
         * Public ctor.
         * @param users Strings with user's login, password and URN with
         *  space characters as separators. For example,
         *  {@code "mike password urn:jcabi-users:michael"}.
         */
        public Default(final String... users) {
            this.usernames = new HashMap<String, String>(users.length);
            for (final String user : users) {
                PsBasic.Default.validateUser(user);
                this.usernames.put(
                    String.format(
                        PsBasic.Default.KEY_FORMAT,
                        user.substring(0, user.indexOf(' ')),
                        user.substring(
                            user.indexOf(' ') + 1,
                            user.lastIndexOf(' ')
                        )
                    ),
                    user.substring(user.lastIndexOf(' ') + 1)
                );
            }
        }

        @Override
        public Opt<Identity> enter(final String user, final String pwd) {
            final String urn = this.usernames.get(
                String.format(Default.KEY_FORMAT, user, pwd)
            );
            final Opt<Identity> identity;
            if (urn == null) {
                identity = new Opt.Empty<Identity>();
            } else {
                identity = new Opt.Single<Identity>(new Identity.Simple(urn));
            }
            return identity;
        }

        /**
         * Checks if a user string is correctly formatted.
         * @param user String with user login, password and urn separated
         *  with spaces.
         */
        private static void validateUser(final String user) {
            final boolean incorrectAmount = Default.countSpaces(user) != 2;
            final boolean nearby =
                user.indexOf(' ') + 1 == user.lastIndexOf(' ');
            if (incorrectAmount || nearby) {
                throw new IllegalArgumentException(
                    String.format(
                        "One of users was incorrectly formatted: %s",
                        user
                    )
                );
            }
        }

        /**
         * Counts spaces in a string.
         * @param string Any string.
         * @return Amount of spaces in string.
         */
        private static int countSpaces(final String string) {
            int spaces = 0;
            for (char character : string.toCharArray()) {
                if (character == ' ') {
                    spaces += 1;
                }
            }
            return spaces;
        }

    }
}
