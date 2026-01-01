/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import jakarta.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.flash.RsFlash;
import org.takes.facets.forward.RsForward;
import org.takes.misc.Opt;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqHref;
import org.takes.rs.RsWithHeader;

/**
 * Pass that authenticates users according to RFC-2617 (HTTP Basic Authentication).
 * This implementation validates user credentials provided via the HTTP Authorization
 * header using Base64-encoded username and password pairs.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.20
 * @todo #863:30min Continue removing nulls from the code base, there are still
 *  some places that use it and can be replaced with better code constructs.
 */
@EqualsAndHashCode
@SuppressWarnings("PMD.TooManyMethods")
public final class PsBasic implements Pass {

    /**
     * Pattern for basic authorization name.
     */
    private static final Pattern AUTH = Pattern.compile("Basic");

    /**
     * Entry to validate user information.
     */
    private final PsBasic.Entry entry;

    /**
     * Realm.
     */
    private final String realm;

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
        final Iterator<String> headers = new RqHeaders.Smart(request)
            .header("authorization").iterator();
        if (!headers.hasNext()) {
            throw new RsForward(
                new RsWithHeader(
                    String.format(
                        "WWW-Authenticate: Basic realm=\"%s\" ",
                        this.realm
                    )
                ),
                HttpURLConnection.HTTP_UNAUTHORIZED,
                new RqHref.Base(request).href()
            );
        }
        final String decoded = new IoCheckedText(
            new Trimmed(
                new TextOf(
                    DatatypeConverter.parseBase64Binary(
                        PsBasic.AUTH.split(headers.next())[1]
                    )
                )
            )
        ).asString();
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
                        "WWW-Authenticate: Basic realm=\"%s\"",
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
    public Response exit(final Response response, final Identity identity) {
        return response;
    }

    /**
     * Entry interface that validates user credentials.
     * Implementations of this interface determine whether a given
     * username and password combination is valid for authentication.
     *
     * @since 0.20
     */
    public interface Entry {
        /**
         * Check if the user credentials are valid.
         * @param user Username
         * @param pwd Password
         * @return Identity if credentials are valid, empty otherwise
         */
        Opt<Identity> enter(String user, String pwd);
    }

    /**
     * Fake implementation of {@link PsBasic.Entry} for testing purposes.
     * This implementation returns a predefined authentication result based
     * on a boolean condition provided during construction.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @since 0.20
     */
    public static final class Fake implements PsBasic.Entry {
        /**
         * Should we authenticate a user?
         */
        private final boolean condition;

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
                user = new Opt.Single<>(
                    new Identity.Simple(
                        String.format("urn:basic:%s", usr)
                    )
                );
            } else {
                user = new Opt.Empty<>();
            }
            return user;
        }
    }

    /**
     * Empty implementation that always denies authentication.
     * This implementation always returns an empty identity,
     * effectively rejecting all authentication attempts.
     *
     * @since 0.20
     */
    public static final class Empty implements PsBasic.Entry {
        @Override
        public Opt<Identity> enter(final String user, final String pwd) {
            return new Opt.Empty<>();
        }
    }

    /**
     * Default entry implementation that validates credentials against
     * a predefined set of username, password, and URN combinations.
     * Credentials are stored as URL-encoded strings separated by spaces.
     *
     * @since 0.22
     */
    public static final class Default implements PsBasic.Entry {
        /**
         * How keys in
         * {@link org.takes.facets.auth.PsBasic.Default#usernames} are
         * formatted.
         */
        private static final String KEY_FORMAT = "%s %s";

        /**
         * Encoding for URLEncode#encode.
         */
        private static final String ENCODING = "UTF-8";

        /**
         * Map from login/password pairs to URNs.
         */
        private final Map<String, String> usernames;

        /**
         * Public ctor.
         * @param users Strings with user's login, password and URNs
         */
        public Default(final Iterable<String> users) {
            this.usernames = Default.converted(users);
        }

        /**
         * Public ctor.
         * @param users Strings with user's login, password and URN with
         *  space characters as separators. Each of login, password and urn
         *  are URL-encoded substrings. For example,
         *  {@code "mike my%20password urn:jcabi-users:michael"}.
         */
        public Default(final String... users) {
            this(Arrays.asList(users));
        }

        @Override
        public Opt<Identity> enter(final String user, final String pwd) {
            final Opt<String> urn = this.urn(user, pwd);
            final Opt<Identity> identity;
            if (urn.has()) {
                try {
                    identity = new Opt.Single<>(
                        new Identity.Simple(
                            URLDecoder.decode(
                                urn.get(), PsBasic.Default.ENCODING
                            )
                        )
                    );
                } catch (final UnsupportedEncodingException ex) {
                    throw new IllegalStateException(
                        String.format("Failed to decode URN '%s'", urn.get()),
                        ex
                    );
                }
            } else {
                identity = new Opt.Empty<>();
            }
            return identity;
        }

        /**
         * Converts Strings with user's login, password and URN to Map.
         * @param users Strings with user's login, password and URN with
         *  space characters as separators. Each of login, password and urn
         *  are URL-encoded substrings. For example,
         *  {@code "mike my%20password urn:jcabi-users:michael"}.
         * @return Map from login/password pairs to URNs.
         */
        private static Map<String, String> converted(final Iterable<String> users) {
            final Map<String, String> result = new HashMap<>(0);
            for (final String user : users) {
                final String unified = user.replace("%20", "+");
                PsBasic.Default.validateUser(unified);
                result.put(
                    PsBasic.Default.key(unified),
                    unified.substring(unified.lastIndexOf(' ') + 1)
                );
            }
            return result;
        }

        /**
         * Returns an URN corresponding to a login-password pair.
         * @param user Login.
         * @param pwd Password.
         * @return Opt with URN or empty if there is no such login-password
         *  pair.
         */
        private Opt<String> urn(final String user, final String pwd) {
            final String urn;
            try {
                urn = this.usernames.get(
                    String.format(
                        PsBasic.Default.KEY_FORMAT,
                        URLEncoder.encode(
                            user,
                            PsBasic.Default.ENCODING
                        ),
                        URLEncoder.encode(
                            pwd,
                            PsBasic.Default.ENCODING
                        )
                    )
                );
            } catch (final UnsupportedEncodingException ex) {
                throw new IllegalStateException(
                    "Failed to encode user name or password",
                    ex
                );
            }
            final Opt<String> opt;
            if (urn == null) {
                opt = new Opt.Empty<>();
            } else {
                opt = new Opt.Single<>(urn);
            }
            return opt;
        }

        /**
         * Creates a key for
         *  {@link org.takes.facets.auth.PsBasic.Default#usernames} map.
         * @param unified User string made of 3 urlencoded substrings
         *  separated with non-urlencoded space characters.
         * @return Login and password parts with <pre>%20</pre> replaced with
         *  <pre>+</pre>.
         */
        private static String key(final String unified) {
            return String.format(
                PsBasic.Default.KEY_FORMAT,
                unified.substring(0, unified.indexOf(' ')),
                unified.substring(
                    unified.indexOf(' ') + 1,
                    unified.lastIndexOf(' ')
                )
            );
        }

        /**
         * Checks if a unified user string is correctly formatted.
         * @param unified String with urlencoded user login, password and urn
         *  separated with spaces.
         */
        private static void validateUser(final String unified) {
            final boolean amount = PsBasic.Default.countSpaces(unified) != 2;
            final boolean nearby =
                unified.indexOf(' ') + 1 == unified.lastIndexOf(' ');
            if (amount || nearby) {
                throw new IllegalArgumentException(
                    String.format(
                        "One of users was incorrectly formatted: %s",
                        unified
                    )
                );
            }
        }

        /**
         * Counts spaces in a string.
         * @param txt Any string.
         * @return Amount of spaces in string.
         */
        private static int countSpaces(final String txt) {
            int spaces = 0;
            for (final char character : txt.toCharArray()) {
                if (character == ' ') {
                    spaces += 1;
                }
            }
            return spaces;
        }
    }
}
