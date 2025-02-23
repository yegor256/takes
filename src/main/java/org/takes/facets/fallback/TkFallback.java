/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
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
 * @checkstyle IllegalCatchCheck (500 lines)
 * @todo #918:30min {@link TkFallback} class is very complicated, hard to read.
 *  Please consider removing static methods and replace them by dedicated
 *  elegant classes according to
 *  https://www.yegor256.com/2017/02/07/private-method-is-new-class.html
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("PMD.AvoidCatchingThrowable")
public final class TkFallback extends TkWrap {

    /**
     * Ctor.
     * @param take Original take
     * @param fbk Fallback
     */
    public TkFallback(final Take take, final Fallback fbk) {
        super(
            req -> TkFallback.route(take, fbk, req)
        );
    }

    /**
     * Route this request.
     * @param take The take
     * @param fbk Fallback
     * @param req Request
     * @return Response
     * @throws Exception If fails
     */
    private static Response route(final Take take, final Fallback fbk,
        final Request req) throws Exception {
        final long start = System.currentTimeMillis();
        Response res;
        try {
            res = TkFallback.wrap(
                take.act(req), fbk, req
            );
        } catch (final HttpException ex) {
            final Opt<Response> fbres = fbk.route(
                TkFallback.fallback(req, start, ex, ex.code())
            );
            if (!fbres.has()) {
                throw new IOException(
                    String.format(
                        "There is no fallback available in %s",
                        fbk.getClass().getCanonicalName()
                    ),
                    TkFallback.error(ex, req, start)
                );
            }
            res = TkFallback.wrap(fbres.get(), fbk, req);
        } catch (final Throwable ex) {
            final Opt<Response> fbres = fbk.route(
                TkFallback.fallback(
                    req, start, ex,
                    HttpURLConnection.HTTP_INTERNAL_ERROR
                )
            );
            if (!fbres.has()) {
                throw new IOException(
                    String.format(
                        "There is no fallback available for %s in %s",
                        ex.getClass().getCanonicalName(),
                        fbk.getClass().getCanonicalName()
                    ),
                    TkFallback.error(ex, req, start)
                );
            }
            res = TkFallback.wrap(
                fbres.get(),
                fbk, req
            );
        }
        return res;
    }

    /**
     * Fallback request.
     * @param req Request
     * @param start Start time of request processing
     * @param throwable Exception thrown
     * @param code Error code
     * @return Fallback request
     * @throws IOException In case of error
     * @checkstyle ParameterNumber (3 lines)
     */
    private static RqFallback.Fake fallback(final Request req, final long start,
        final Throwable throwable, final int code) throws IOException {
        return new RqFallback.Fake(
            req, code, TkFallback.error(throwable, req, start)
        );
    }

    /**
     * Wrap response.
     * @param res Response to wrap
     * @param fbk Fallback
     * @param req Request
     * @return Response
     * @checkstyle ExecutableStatementCountCheck (100 lines)
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private static Response wrap(final Response res, final Fallback fbk,
        final Request req) {
        return new ResponseOf(
            () -> {
                final long start = System.currentTimeMillis();
                Iterable<String> head;
                try {
                    head = res.head();
                } catch (final HttpException ex) {
                    try {
                        head = fbk.route(
                            TkFallback.fallback(req, start, ex, ex.code())
                        ).get().head();
                    } catch (final Exception exx) {
                        throw (IOException) new IOException(exx).initCause(ex);
                    }
                } catch (final Throwable ex) {
                    try {
                        head = fbk.route(
                            TkFallback.fallback(
                                req, start, ex,
                                HttpURLConnection.HTTP_INTERNAL_ERROR
                            )
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
                    body = res.body();
                } catch (final HttpException ex) {
                    try {
                        body = fbk.route(
                            TkFallback.fallback(req, start, ex, ex.code())
                        ).get().body();
                    } catch (final Exception exx) {
                        throw (IOException) new IOException(exx).initCause(ex);
                    }
                } catch (final Throwable ex) {
                    try {
                        body = fbk.route(
                            TkFallback.fallback(
                                req, start, ex,
                                HttpURLConnection.HTTP_INTERNAL_ERROR
                            )
                        ).get().body();
                    } catch (final Exception exx) {
                        throw (IOException) new IOException(exx).initCause(ex);
                    }
                }
                return body;
            }
        );
    }

    /**
     * Create an error.
     * @param exp Exception original
     * @param req Request we're processing
     * @param start When started
     * @return Error
     * @throws IOException If fails
     */
    private static Throwable error(final Throwable exp, final Request req,
        final long start) throws IOException {
        final String time;
        final long msec = System.currentTimeMillis() - start;
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
                new RqMethod.Base(req).method(),
                new RqHref.Base(req).href(),
                time, TkFallback.msg(exp)
            ),
            exp
        );
    }

    /**
     * Get full error message from the exception and all its kids.
     * @param exp Exception original
     * @return Error message
     */
    private static String msg(final Throwable exp) {
        final StringBuilder txt = new StringBuilder(0);
        final String localized = exp.getLocalizedMessage();
        if (localized == null) {
            txt.append("NULL");
        } else {
            txt.append(localized);
        }
        final Throwable cause = exp.getCause();
        if (cause != null) {
            txt.append("; ");
            txt.append(TkFallback.msg(cause));
        }
        return txt.toString();
    }

}
