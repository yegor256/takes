/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import jakarta.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.cactoos.text.FormattedText;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;
import org.cactoos.text.UncheckedText;
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
                    new UncheckedText(
                        new FormattedText(
                            "WWW-Authenticate: Basic realm=\"%s\" ",
                            this.realm
                        )
                    ).asString()
                ),
                HttpURLConnection.HTTP_UNAUTHORIZED,
                new RqHref.Base(request).href()
            );
        }
        final String decoded = new IoCheckedText(
            new Trimmed(
                new TextOf(
                    DatatypeConverter.parseBase64Binary(
                        PsBasic.AUTH.split(headers.next(), 2)[1]
                    )
                )
            )
        ).asString();
        final String user = decoded.split(":", 2)[0];
        final Opt<Identity> identity = this.entry.enter(
            user,
            decoded.substring(user.length() + 1)
        );
        if (!identity.has()) {
            throw new RsForward(
                new RsWithHeader(
                    new RsFlash("access denied", Level.WARNING),
                    new UncheckedText(
                        new FormattedText(
                            "WWW-Authenticate: Basic realm=\"%s\"",
                            this.realm
                        )
                    ).asString()
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
     * @since 0.20
     */
    @FunctionalInterface
    public interface Entry {

        /**
         * Check if the user credentials are valid.
         * @param user Username
         * @param pwd Password
         * @return Identity if credentials are valid, empty otherwise
         */
        Opt<Identity> enter(String user, String pwd);
    }
}
