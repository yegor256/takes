/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cactoos.Scalar;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqRequestLine;
import org.takes.rs.RsHeaders;
import org.takes.rs.RsStatus;

/**
 * Decision whether a connection stays open after an exchange.
 *
 * <p>A back-end may read the next request from the same connection only
 * when both peers agreed to keep it and the client can tell where the
 * response ends. The connection persists by default for HTTP/1.1, only
 * on explicit keep-alive for HTTP/1.0, never when either side asked to
 * close it, and never when the response carries no length and is
 * delimited by the end of the connection. Anything the parsers of takes
 * reject means close, so an odd request never keeps a connection by
 * accident.</p>
 *
 * @since 1.20
 * @todo #1748:30min Keep the connection open after a response without a
 *  body length, such as RsRedirect, RsEmpty or RsWithStatus, by adding
 *  Content-Length: 0 when the body is known to be empty, or chunked
 *  transfer coding otherwise.
 * @todo #1748:30min Close the connection when the response has no head
 *  at all. RsStatus.Base throws NoSuchElementException on an empty head,
 *  which value() does not catch, so the connection drops without any
 *  response instead of being treated as non-persistent.
 */
final class Persistence implements Scalar<Boolean> {

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
        boolean persistent;
        try {
            final Collection<String> asked = Persistence.tokens(
                new RqHeaders.Base(this.request).header("Connection")
            );
            if (asked.contains("close")
                || Persistence.tokens(
                    new RsHeaders.Base(this.response).header("Connection")
                ).contains("close")
                || !this.delimited()) {
                persistent = false;
            } else {
                final String version = new RqRequestLine.Base(this.request)
                    .version();
                persistent = "HTTP/1.1".equals(version)
                    || "HTTP/1.0".equals(version)
                    && asked.contains("keep-alive");
            }
        } catch (final HttpException | IllegalArgumentException ex) {
            persistent = false;
        }
        return persistent;
    }

    private boolean delimited() throws IOException {
        final RsHeaders headers = new RsHeaders.Base(this.response);
        final int status = new RsStatus.Base(this.response).status();
        return !headers.header("Content-Length").isEmpty()
            || !headers.header("Transfer-Encoding").isEmpty()
            || status == HttpURLConnection.HTTP_NO_CONTENT
            || status == HttpURLConnection.HTTP_NOT_MODIFIED;
    }

    private static Collection<String> tokens(final Iterable<String> values) {
        final Collection<String> found = new HashSet<>(0);
        for (final String value : values) {
            final Matcher token = Persistence.TOKEN.matcher(value);
            while (token.find()) {
                found.add(token.group().trim().toLowerCase(Locale.ENGLISH));
            }
        }
        return found;
    }
}
