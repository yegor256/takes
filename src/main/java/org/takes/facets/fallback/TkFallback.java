/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Opt;
import org.takes.rq.RqHref;
import org.takes.rq.RqMethod;
import org.takes.rs.ResponseOf;
import org.takes.tk.TkWrap;

/**
 * Fallback.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 * @todo #918:30min {@link TkFallback} class is very complicated, hard to read.
 *  Please consider removing static methods and replace them by dedicated
 *  elegant classes according to
 *  https://www.yegor256.com/2017/02/07/private-method-is-new-class.html
 * @checkstyle IllegalCatchCheck (500 lines)
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("PMD.AvoidCatchingThrowable")
public final class TkFallback extends TkWrap {

    public TkFallback(final Take take, final Fallback fbk) {
        super(
            req -> new Route(take, fbk, req).route()
        );
    }

    /**
     * Handles routing logic previously in static method.
     */
    private static final class Route {
        private final Take take;
        private final Fallback fbk;
        private final Request req;
        Route(Take take, Fallback fbk, Request req) {
            this.take = take;
            this.fbk = fbk;
            this.req = req;
        }
        Response route() throws Exception {
            final long start = System.currentTimeMillis();
            Response res;
            try {
                res = new Wrap(this.take.act(this.req), this.fbk, this.req).wrap();
            } catch (final HttpException ex) {
                final Opt<Response> fbres = this.fbk.route(
                    new FallbackRequest(this.req, start, ex, ex.code()).fallback()
                );
                if (!fbres.has()) {
                    throw new IOException(
                        String.format(
                            "There is no fallback available in %s",
                            this.fbk.getClass().getCanonicalName()
                        ),
                        new ErrorInfo(ex, this.req, start).error()
                    );
                }
                res = new Wrap(fbres.get(), this.fbk, this.req).wrap();
            } catch (final Throwable ex) {
                final Opt<Response> fbres = this.fbk.route(
                    new FallbackRequest(
                        this.req, start, ex,
                        HttpURLConnection.HTTP_INTERNAL_ERROR
                    ).fallback()
                );
                if (!fbres.has()) {
                    throw new IOException(
                        String.format(
                            "There is no fallback available for %s in %s",
                            ex.getClass().getCanonicalName(),
                            this.fbk.getClass().getCanonicalName()
                        ),
                        new ErrorInfo(ex, this.req, start).error()
                    );
                }
                res = new Wrap(fbres.get(), this.fbk, this.req).wrap();
            }
            return res;
        }
    }

    /**
     * Handles fallback request creation.
     */
    private static final class FallbackRequest {
        private final Request req;
        private final long start;
        private final Throwable throwable;
        private final int code;
        FallbackRequest(Request req, long start, Throwable throwable, int code) {
            this.req = req;
            this.start = start;
            this.throwable = throwable;
            this.code = code;
        }
        RqFallback.Fake fallback() throws IOException {
            return new RqFallback.Fake(
                this.req, this.code, new ErrorInfo(this.throwable, this.req, this.start).error()
            );
        }
    }

    /**
     * Handles response wrapping logic.
     */
    private static final class Wrap {
        private final Response res;
        private final Fallback fbk;
        private final Request req;
        Wrap(Response res, Fallback fbk, Request req) {
            this.res = res;
            this.fbk = fbk;
            this.req = req;
        }
        Response wrap() {
            return new ResponseOf(
                () -> {
                    final long start = System.currentTimeMillis();
                    Iterable<String> head;
                    try {
                        head = this.res.head();
                    } catch (final HttpException ex) {
                        try {
                            head = this.fbk.route(
                                new FallbackRequest(this.req, start, ex, ex.code()).fallback()
                            ).get().head();
                        } catch (final Exception exx) {
                            throw (IOException) new IOException(exx).initCause(ex);
                        }
                    } catch (final Throwable ex) {
                        try {
                            head = this.fbk.route(
                                new FallbackRequest(
                                    this.req, start, ex,
                                    HttpURLConnection.HTTP_INTERNAL_ERROR
                                ).fallback()
                            ).get().head();
                        } catch (final Exception exx) {
                            throw (IOException) new IOException(exx).initCause(ex);
                        }
                    }
                    return head;
                },
                () -> {
                    final long start = System.currentTimeMillis();
                    InputStream body;
                    try {
                        body = this.res.body();
                    } catch (final HttpException ex) {
                        try {
                            body = this.fbk.route(
                                new FallbackRequest(this.req, start, ex, ex.code()).fallback()
                            ).get().body();
                        } catch (final Exception exx) {
                            throw (IOException) new IOException(exx).initCause(ex);
                        }
                    } catch (final Throwable ex) {
                        try {
                            body = this.fbk.route(
                                new FallbackRequest(
                                    this.req, start, ex,
                                    HttpURLConnection.HTTP_INTERNAL_ERROR
                                ).fallback()
                            ).get().body();
                        } catch (final Exception exx) {
                            throw (IOException) new IOException(exx).initCause(ex);
                        }
                    }
                    return body;
                }
            );
        }
    }

    /**
     * Handles error creation logic.
     */
    private static final class ErrorInfo {
        private final Throwable exp;
        private final Request req;
        private final long start;
        ErrorInfo(Throwable exp, Request req, long start) {
            this.exp = exp;
            this.req = req;
            this.start = start;
        }
        Throwable error() throws IOException {
            final String time;
            final long msec = System.currentTimeMillis() - this.start;
            if (msec < TimeUnit.SECONDS.toMillis(1L)) {
                time = String.format("%dms", msec);
            } else {
                time = String.format(
                    "%ds",
                    msec / TimeUnit.SECONDS.toMillis(1L)
                );
            }
            return new IllegalStateException(
                String.format(
                    "[%s %s] failed in %s: %s",
                    new RqMethod.Base(this.req).method(),
                    new RqHref.Base(this.req).href(),
                    time, new ExceptionMessage(this.exp).msg()
                ),
                this.exp
            );
        }
    }

    /**
     * Handles exception message extraction logic.
     */
    private static final class ExceptionMessage {
        private final Throwable exp;
        ExceptionMessage(Throwable exp) {
            this.exp = exp;
        }
        String msg() {
            final StringBuilder txt = new StringBuilder(0);
            final String localized = this.exp.getLocalizedMessage();
            if (localized == null) {
                txt.append("NULL");
            } else {
                txt.append(localized);
            }
            final Throwable cause = this.exp.getCause();
            if (cause != null) {
                txt.append("; ");
                txt.append(new ExceptionMessage(cause).msg());
            }
            return txt.toString();
        }
    }
}
