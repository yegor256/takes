/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.forward;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;

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
        return new Safe(res);
    }
}
