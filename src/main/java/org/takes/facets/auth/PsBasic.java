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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.xml.bind.DatatypeConverter;
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
        Opt<BasicAuth> auth = new Opt.Empty<PsBasic.BasicAuth>();
        Opt<Identity> identity = new Opt.Empty<Identity>();
        for (final String head : request.head()) {
            if (head.startsWith(AUTH_HEAD)) {
                auth = this.readAuthContentOnHead(head);
                break;
            }
        }
        if (auth.has() && this.entry.check(
            auth.get().username(),
            auth.get().password()
        ).has()) {
            final ConcurrentMap<String, String> props =
                new ConcurrentHashMap<String, String>(0);
            identity = new Opt.Single<Identity>(
                new Identity.Simple(
                    String.format("urn:basic:%s", auth.get().username()),
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
     * Read authentication content that is received on the head.
     * @param head Head
     * @return BasicAuth instance.
     */
    private Opt<BasicAuth> readAuthContentOnHead(final String head) {
        final String authorization = new String(
            DatatypeConverter.parseBase64Binary(
                head.split(AUTH_HEAD)[1].trim()
            )
        );
        final String user = authorization.split(":")[0];
        return new Opt.Single<PsBasic.BasicAuth>(
            new BasicAuth(
                user,
                authorization.substring(user.length() + 1)
            )
        );
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
        Opt<Identity> check(String user, String pwd);
    }

    /**
     * Entry with fixed credentials.
     *
     * @author Endrigo Antonini (teamed@endrigo.com.br)
     * @version $Id$
     * @since 0.20
     *
     */
    public static final class Fixed implements PsBasic.Entry {

        /**
         * Username.
         */
        private final transient String username;

        /**
         * Password.
         */
        private final transient String password;

        /**
         * Ctor.
         * @param user Username.
         * @param pwd Password.
         */
        public Fixed(final String user, final String pwd) {
            this.username = user;
            this.password = pwd;
        }

        @Override
        public Opt<Identity> check(final String user, final String pwd) {
            final Opt<Identity> identity;
            if (this.username.equals(user) && this.password.equals(pwd)) {
                identity = new Opt.Single<Identity>(
                        new Identity.Simple(user)
                );
            } else {
                identity = new Opt.Empty<Identity>();
            }
            return identity;
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
        public Opt<Identity> check(final String user, final String pwd) {
            return new Opt.Empty<Identity>();
        }
    }

    /**
     * Used to transfer authentication information.
     *
     * @author Endrigo Antonini (teamed@endrigo.com.br)
     * @version $Id$
     * @since 0.20
     */
    private final class BasicAuth {

        /**
         * User.
         */
        private final transient String user;

        /**
         * Password.
         */
        private final transient String pass;

        /**
         * Ctor.
         * @param username User
         * @param password Password
         */
        public BasicAuth(final String username, final String password) {
            super();
            this.user = username;
            this.pass = password;
        }

        /**
         * Return user.
         * @return User.
         */
        public String username() {
            return this.user;
        }

        /**
         * Return Password.
         * @return Password.
         */
        public String password() {
            return this.pass;
        }
    }
}
