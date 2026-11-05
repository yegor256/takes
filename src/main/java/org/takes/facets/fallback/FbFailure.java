/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;
import org.takes.Request;
import org.takes.rq.RqHref;
import org.takes.rq.RqMethod;

/**
 * Failure of a request, described for the fallback.
 *
 * <p>The class is immutable and thread-safe.</p>
 *
 * @since 2.0
 */
final class FbFailure {

    /**
     * The exception.
     */
    private final Throwable origin;

    /**
     * The request.
     */
    private final Request request;

    /**
     * Time when the request started, in milliseconds.
     */
    private final long start;

    /**
     * Ctor.
     *
     * @param exp The exception
     * @param req The request
     * @param begin Time when the request started
     */
    FbFailure(final Throwable exp, final Request req, final long begin) {
        this.origin = exp;
        this.request = req;
        this.start = begin;
    }

    /**
     * Request for the fallback.
     *
     * @param code HTTP status code
     * @return The request
     * @throws IOException If fails
     */
    RqFallback.Fake fake(final int code) throws IOException {
        return new RqFallback.Fake(this.request, code, this.error());
    }

    /**
     * Error that describes the failure.
     *
     * @return The error
     * @throws IOException If fails
     */
    Throwable error() throws IOException {
        final String time;
        final long msec = System.currentTimeMillis() - this.start;
        if (msec < TimeUnit.SECONDS.toMillis(1L)) {
            time = new UncheckedText(new FormattedText("%dms", msec)).asString();
        } else {
            time = new UncheckedText(
                new FormattedText(
                    "%ds",
                    msec / TimeUnit.SECONDS.toMillis(1L)
                )
            ).asString();
        }
        return new IllegalStateException(
            new UncheckedText(
                new FormattedText(
                    "[%s %s] failed in %s: %s",
                    new RqMethod.Base(this.request).method(),
                    new RqHref.Base(this.request).href(),
                    time, new FbMessage(this.origin).asString()
                )
            ).asString(),
            this.origin
        );
    }
}
