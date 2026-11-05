/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;

/**
 * Response that asks the fallback for help when its head or body fails.
 *
 * <p>The class is immutable and thread-safe.</p>
 *
 * @since 2.0
 * @checkstyle IllegalCatchCheck (500 lines)
 */
final class FbResponse implements Response {

    /**
     * Original response.
     */
    private final Response origin;

    /**
     * Fallback.
     */
    private final Fallback fallback;

    /**
     * Request.
     */
    private final Request request;

    /**
     * Ctor.
     *
     * @param res Original response
     * @param fbk Fallback
     * @param req Request
     */
    FbResponse(final Response res, final Fallback fbk, final Request req) {
        this.origin = res;
        this.fallback = fbk;
        this.request = req;
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.guard(Response::head);
    }

    @Override
    public InputStream body() throws IOException {
        return this.guard(Response::body);
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private <T> T guard(final Part<T> part) throws IOException {
        final long start = System.currentTimeMillis();
        T result;
        try {
            result = part.of(this.origin);
        } catch (final HttpException ex) {
            result = this.rescue(
                part, new FbFailure(ex, this.request, start).fake(ex.code()), ex
            );
        // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Throwable ex) {
            result = this.rescue(
                part,
                new FbFailure(ex, this.request, start).fake(
                    HttpURLConnection.HTTP_INTERNAL_ERROR
                ),
                ex
            );
        }
        return result;
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private <T> T rescue(final Part<T> part, final RqFallback req,
        final Throwable cause) throws IOException {
        try {
            return part.of(this.fallback.route(req).get());
        } catch (final Exception ex) {
            throw (IOException) new IOException(ex).initCause(cause);
        }
    }

    /**
     * Part of a response.
     *
     * @param <T> Type of the part
     * @since 2.0
     */
    @FunctionalInterface
    private interface Part<T> {

        /**
         * Take it.
         *
         * @param res The response
         * @return The part
         * @throws IOException If fails
         */
        T of(Response res) throws IOException;
    }
}
