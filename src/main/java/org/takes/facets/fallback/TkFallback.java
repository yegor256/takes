/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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
import org.takes.tk.TkWrap;

/**
 * Fallback.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle IllegalCatchCheck (500 lines)
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
            new Take() {
                @Override
                public Response act(final Request req) throws IOException {
                    return TkFallback.route(take, fbk, req);
                }
            }
        );
    }

    /**
     * Route this request.
     * @param take The take
     * @param fbk Fallback
     * @param req Request
     * @return Response
     * @throws IOException If fails
     */
    private static Response route(final Take take, final Fallback fbk,
        final Request req) throws IOException {
        final long start = System.currentTimeMillis();
        Response res;
        try {
            res = TkFallback.wrap(
                take.act(req), fbk, req
            );
        } catch (final HttpException ex) {
            final Opt<Response> fbres = fbk.route(
                new RqFallback.Fake(
                    req, ex.code(),
                    TkFallback.error(
                        ex, req,
                        System.currentTimeMillis() - start
                    )
                )
            );
            if (!fbres.has()) {
                throw new IOException(
                    String.format(
                        "There is no fallback available in %s",
                        fbk.getClass().getCanonicalName()
                    ),
                    TkFallback.error(
                        ex, req,
                        System.currentTimeMillis() - start
                    )
                );
            }
            res = TkFallback.wrap(fbres.get(), fbk, req);
        } catch (final Throwable ex) {
            final Opt<Response> fbres = fbk.route(
                new RqFallback.Fake(
                    req, HttpURLConnection.HTTP_INTERNAL_ERROR,
                    TkFallback.error(
                        ex, req,
                        System.currentTimeMillis() - start
                    )
                )
            );
            if (!fbres.has()) {
                throw new IOException(
                    String.format(
                        "There is no fallback available for %s in %s",
                        ex.getClass().getCanonicalName(),
                        fbk.getClass().getCanonicalName()
                    ),
                    TkFallback.error(
                        ex, req,
                        System.currentTimeMillis() - start
                    )
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
     * Wrap response.
     * @param res Response to wrap
     * @param fbk Fallback
     * @param req Request
     * @return Response
     */
    private static Response wrap(final Response res, final Fallback fbk,
        final Request req) {
        // @checkstyle AnonInnerLengthCheck (50 lines)
        return new Response() {
            @Override
            public Iterable<String> head() throws IOException {
                final long start = System.currentTimeMillis();
                Iterable<String> head;
                try {
                    head = res.head();
                } catch (final HttpException ex) {
                    head = fbk.route(
                        new RqFallback.Fake(
                            req, ex.code(),
                            TkFallback.error(
                                ex, req,
                                System.currentTimeMillis() - start
                            )
                        )
                    ).get().head();
                } catch (final Throwable ex) {
                    head = fbk.route(
                        new RqFallback.Fake(
                            req, HttpURLConnection.HTTP_INTERNAL_ERROR,
                            TkFallback.error(
                                ex, req,
                                System.currentTimeMillis() - start
                            )
                        )
                    ).get().head();
                }
                return head;
            }
            @Override
            public InputStream body() throws IOException {
                final long start = System.currentTimeMillis();
                InputStream body;
                try {
                    body = res.body();
                } catch (final HttpException ex) {
                    body = fbk.route(
                        new RqFallback.Fake(
                            req, ex.code(),
                            TkFallback.error(
                                ex, req,
                                System.currentTimeMillis() - start
                            )
                        )
                    ).get().body();
                } catch (final Throwable ex) {
                    body = fbk.route(
                        new RqFallback.Fake(
                            req, HttpURLConnection.HTTP_INTERNAL_ERROR,
                            TkFallback.error(
                                ex, req,
                                System.currentTimeMillis() - start
                            )
                        )
                    ).get().body();
                }
                return body;
            }
        };
    }

    /**
     * Create an error.
     * @param exp Exception original
     * @param req Request we're processing
     * @param msec Milliseconds it took
     * @return Error
     * @throws IOException If fails
     */
    private static Throwable error(final Throwable exp, final Request req,
        final long msec) throws IOException {
        final String time;
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
                time, exp.getLocalizedMessage()
            ),
            exp
        );
    }

}
