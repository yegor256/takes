/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.cactoos.iterable.IterableOf;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;
import org.takes.misc.Opt;

/**
 * Default entry implementation that validates credentials against
 * a predefined set of username, password, and URN combinations.
 * Credentials are stored as URL-encoded strings separated by spaces.
 * @since 0.22
 */
public final class PsBasicDefault implements PsBasic.Entry {

    /**
     * How keys in
     * {@link org.takes.facets.auth.PsBasicDefault#usernames} are
     * formatted.
     */
    private static final String KEY_FORMAT = "%s %s";

    /**
     * Encoding for URLEncode#encode.
     */
    private static final Charset ENCODING = StandardCharsets.UTF_8;

    /**
     * Map from login/password pairs to URNs.
     */
    private final Unchecked<Map<String, String>> usernames;

    /**
     * Public ctor.
     * @param users Strings with user's login, password and URN with
     *  space characters as separators. Each of login, password and urn
     *  are URL-encoded substrings. For example,
     *  {@code "mike my%20password urn:jcabi-users:michael"}
     */
    public PsBasicDefault(final String... users) {
        this(new IterableOf<>(users));
    }

    /**
     * Primary ctor.
     * @param users Strings with user's login, password and URNs
     */
    public PsBasicDefault(final Iterable<String> users) {
        this.usernames = new Unchecked<>(
            new Sticky<>(() -> PsBasicDefault.converted(users))
        );
    }

    @Override
    public Opt<Identity> enter(final String user, final String pwd) {
        final Opt<String> urn = this.urn(user, pwd);
        final Opt<Identity> identity;
        if (urn.has()) {
            identity = new Opt.Single<>(
                new Identity.Simple(
                    URLDecoder.decode(urn.get(), PsBasicDefault.ENCODING)
                )
            );
        } else {
            identity = new Opt.Empty<>();
        }
        return identity;
    }

    private static Map<String, String> converted(final Iterable<String> users) {
        final Map<String, String> result = new HashMap<>(0);
        for (final String user : users) {
            final String unified = user.replace("%20", "+");
            PsBasicDefault.validateUser(unified);
            result.put(
                PsBasicDefault.key(unified),
                unified.substring(unified.lastIndexOf(' ') + 1)
            );
        }
        return result;
    }

    private Opt<String> urn(final String user, final String pwd) {
        final String urn = this.usernames.value().get(
            new UncheckedText(
                new FormattedText(
                    PsBasicDefault.KEY_FORMAT,
                    URLEncoder.encode(user, PsBasicDefault.ENCODING),
                    URLEncoder.encode(pwd, PsBasicDefault.ENCODING)
                )
            ).asString()
        );
        final Opt<String> opt;
        if (urn == null) {
            opt = new Opt.Empty<>();
        } else {
            opt = new Opt.Single<>(urn);
        }
        return opt;
    }

    private static String key(final String unified) {
        return new UncheckedText(
            new FormattedText(
                PsBasicDefault.KEY_FORMAT,
                unified.substring(0, unified.indexOf(' ')),
                unified.substring(
                    unified.indexOf(' ') + 1,
                    unified.lastIndexOf(' ')
                )
            )
        ).asString();
    }

    private static void validateUser(final String unified) {
        final boolean amount = PsBasicDefault.countSpaces(unified) != 2;
        final boolean nearby =
            unified.indexOf(' ') + 1 == unified.lastIndexOf(' ');
        if (amount || nearby) {
            throw new IllegalArgumentException(
                new UncheckedText(
                    new FormattedText(
                        "One of users was incorrectly formatted: %s",
                        unified
                    )
                ).asString()
            );
        }
    }

    private static int countSpaces(final String txt) {
        int spaces = 0;
        for (int idx = 0; idx < txt.length(); idx += 1) {
            if (txt.charAt(idx) == ' ') {
                spaces += 1;
            }
        }
        return spaces;
    }
}
