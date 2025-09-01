/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.forward;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsSimple;

/**
 * A take decorator that handles RsForward exceptions by converting them to responses.
 *
 * <p>This decorator catches {@link RsForward} exceptions thrown by wrapped takes
 * and converts them into proper HTTP redirect responses. It enables the use of
 * exception-based flow control for redirects, making error handling and navigation
 * logic more convenient. The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(of = "origin")
@EqualsAndHashCode
public final class TkForward implements Take {

    /**
     * Original take.
     */
    private final Take origin;

    /**
     * Constructor that wraps a take to handle RsForward exceptions.
     * @param take The original take to wrap
     */
    public TkForward(final Take take) {
        this.origin = take;
    }

    @Override
    public Response act(final Request req) throws Exception {
        Response res;
        try {
            res = this.origin.act(req);
        } catch (final RsForward ex) {
            res = ex;
        }
        return new TkForward.Safe(res);
    }

    /**
     * A safe response wrapper that handles RsForward exceptions during response processing.
     * @since 0.1
     */
    @ToString(of = { "origin", "saved" })
    private static final class Safe implements Response {
        /**
         * Original response.
         */
        private final Response origin;

        /**
         * Saved response.
         */
        private final List<Response> saved;

        /**
         * Constructor for safe response wrapper.
         * @param res The original response to wrap safely
         */
        private Safe(final Response res) {
            this.origin = res;
            this.saved = new CopyOnWriteArrayList<>();
        }

        @Override
        public Iterable<String> head() throws IOException {
            return this.load().head();
        }

        @Override
        public InputStream body() throws IOException {
            return this.load().body();
        }

        /**
         * Loads the response, handling any RsForward exceptions that occur.
         * @return The loaded response
         * @throws IOException If response loading fails
         */
        @SuppressWarnings("PMD.CloseResource")
        private Response load() throws IOException {
            if (this.saved.isEmpty()) {
                Iterable<String> head;
                InputStream body;
                try {
                    head = this.origin.head();
                    body = this.origin.body();
                } catch (final RsForward ex) {
                    head = ex.head();
                    body = ex.body();
                }
                this.saved.add(new RsSimple(head, body));
            }
            return this.saved.get(0);
        }
    }

}
