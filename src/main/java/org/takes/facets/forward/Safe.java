/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.forward;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.ToString;
import org.takes.Response;
import org.takes.rs.RsSimple;

/**
 * A safe response wrapper that handles RsForward exceptions during response processing.
 * @since 0.1
 */
@ToString(of = { "origin", "saved" })
final class Safe implements Response {

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
    Safe(final Response res) {
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
