/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import java.io.IOException;
import java.net.HttpURLConnection;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Opt;

/**
 * Take that routes every failure of another take to a fallback.
 *
 * <p>The class is immutable and thread-safe.</p>
 *
 * @since 2.0
 * @checkstyle IllegalCatchCheck (500 lines)
 */
final class FbRoute implements Take {

    /**
     * Original take.
     */
    private final Take origin;

    /**
     * Fallback.
     */
    private final Fallback fallback;

    /**
     * Ctor.
     *
     * @param take Original take
     * @param fbk Fallback
     */
    FbRoute(final Take take, final Fallback fbk) {
        this.origin = take;
        this.fallback = fbk;
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public Response act(final Request req) throws Exception {
        final long start = System.currentTimeMillis();
        Response res;
        try {
            res = new FbResponse(this.origin.act(req), this.fallback, req);
        } catch (final HttpException ex) {
            final FbFailure failure = new FbFailure(ex, req, start);
            final Opt<Response> fbres = this.fallback.route(
                failure.fake(ex.code())
            );
            if (!fbres.has()) {
                throw new IOException(
                    new UncheckedText(
                        new FormattedText(
                            "There is no fallback available in %s",
                            this.fallback.getClass().getCanonicalName()
                        )
                    ).asString(),
                    failure.error()
                );
            }
            res = new FbResponse(fbres.get(), this.fallback, req);
        // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Throwable ex) {
            final FbFailure failure = new FbFailure(ex, req, start);
            final Opt<Response> fbres = this.fallback.route(
                failure.fake(HttpURLConnection.HTTP_INTERNAL_ERROR)
            );
            if (!fbres.has()) {
                throw new IOException(
                    new UncheckedText(
                        new FormattedText(
                            "There is no fallback available for %s in %s",
                            ex.getClass().getCanonicalName(),
                            this.fallback.getClass().getCanonicalName()
                        )
                    ).asString(),
                    failure.error()
                );
            }
            res = new FbResponse(fbres.get(), this.fallback, req);
        }
        return res;
    }
}
