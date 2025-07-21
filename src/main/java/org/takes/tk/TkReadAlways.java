/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;

/**
 * A Take decorator which reads and ignores the request body.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.30
 */
@ToString(of = "origin")
@EqualsAndHashCode
public final class TkReadAlways implements Take {

    /**
     * Original take.
     */
    private final Take origin;

    /**
     * Ctor.
     * @param take Original take
     */
    public TkReadAlways(final Take take) {
        this.origin = take;
    }

    @Override
    public Response act(final Request req) throws Exception {
        final Response res = this.origin.act(req);
        for (int count = req.body().available(); count > 0;
            count = req.body().available()) {
            if (req.body().skip((long) count) < (long) count) {
                break;
            }
        }
        return res;
    }

}
