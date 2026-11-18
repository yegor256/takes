/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cactoos.Scalar;
import org.takes.Request;
import org.takes.Response;

/**
 * Decision whether a connection stays open after an exchange.
 *
 * <p>A back-end may read the next request from the same connection only
 * when both peers agreed to keep it and the client can tell where the
 * response ends. The connection persists by default for HTTP/1.1, only
 * on explicit keep-alive for HTTP/1.0, never when either side asked to
 * close it, and never when the response carries no length and is
 * delimited by the end of the connection. Anything unrecognised means
 * close, so an odd request never keeps a connection by accident.</p>
 *
 * @since 1.20
 */
final class Persistence implements Scalar<Boolean> {

    /**
     * Response line whose status forbids a body.
     */
    private static final Pattern BODILESS = Pattern.compile(
        "\\s*\\S+\\s+(?:204|304)(?:\\s|$)"
    );

    /**
     * Header token, anything between commas.
     */
    private static final Pattern TOKEN = Pattern.compile("[^,]+");

    /**
     * Request that was answered.
     */
    private final Request request;

    /**
     * Response that answers it.
     */
    private final Response response;

    /**
     * Ctor.
     *
     * @param req Request that was answered
     * @param res Response that answers it
     */
    Persistence(final Request req, final Response res) {
        this.request = req;
        this.response = res;
    }

    @Override
    public Boolean value() throws IOException {
        final Collection<String> asked = Persistence.tokens(
            this.request.head(), "connection"
        );
        final boolean persistent;
        if (asked.contains("close")
            || Persistence.tokens(this.response.head(), "connection")
                .contains("close")
            || !this.delimited()) {
            persistent = false;
        } else {
            final String line = Persistence.first(this.request.head());
            persistent = line.endsWith(" HTTP/1.1")
                || line.endsWith(" HTTP/1.0") && asked.contains("keep-alive");
        }
        return persistent;
    }

    private boolean delimited() throws IOException {
        final boolean delimited;
        if (Persistence.tokens(this.response.head(), "content-length").isEmpty()
            && Persistence.tokens(this.response.head(), "transfer-encoding")
                .isEmpty()) {
            delimited = Persistence.BODILESS.matcher(
                Persistence.first(this.response.head())
            ).lookingAt();
        } else {
            delimited = true;
        }
        return delimited;
    }

    private static String first(final Iterable<String> head) {
        final Iterator<String> lines = head.iterator();
        final String line;
        if (lines.hasNext()) {
            line = lines.next();
        } else {
            line = "";
        }
        return line;
    }

    private static Collection<String> tokens(final Iterable<String> head,
        final String name) {
        final Collection<String> found = new HashSet<>(0);
        final Iterator<String> lines = head.iterator();
        if (lines.hasNext()) {
            lines.next();
        }
        while (lines.hasNext()) {
            final String line = lines.next();
            final int colon = line.indexOf(':');
            if (colon > 0 && name.equals(
                line.substring(0, colon).trim().toLowerCase(Locale.ENGLISH)
            )) {
                final Matcher token = Persistence.TOKEN.matcher(
                    line.substring(colon + 1)
                );
                while (token.find()) {
                    found.add(token.group().trim().toLowerCase(Locale.ENGLISH));
                }
            }
        }
        return found;
    }
}
