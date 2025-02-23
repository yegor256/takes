/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.apache.log4j.Logger;
import org.cactoos.bytes.BytesOf;
import org.cactoos.text.TextOf;
import org.takes.misc.Opt;
import org.takes.rq.RqHref;
import org.takes.rq.RqMethod;

/**
 * Fallback that logs all problems through Log4J.
 * @since 0.25
 */
@EqualsAndHashCode(callSuper = true)
public final class FbLog4j extends FbWrap {

    /**
     * Ctor.
     */
    public FbLog4j() {
        super(
            req -> {
                FbLog4j.log(req);
                return new Opt.Empty<>();
            }
        );
    }

    /**
     * Log this request.
     * @param req Request
     * @throws IOException If fails
     */
    private static void log(final RqFallback req) throws IOException {
        Logger.getLogger(FbLog4j.class).error(
            String.format(
                "%s %s failed with %s: %s",
                new RqMethod.Base(req).method(),
                new RqHref.Base(req).href(),
                req.code(),
                new TextOf(new BytesOf(req.throwable()))
            )
        );
    }
}
