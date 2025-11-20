/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.cactoos.bytes.BytesOf;
import org.cactoos.text.TextOf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.takes.misc.Opt;
import org.takes.rq.RqHref;
import org.takes.rq.RqMethod;

/**
 * Fallback that logs all problems through SFL4J.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.25
 */
@EqualsAndHashCode(callSuper = true)
public final class FbSlf4j extends FbWrap {

    /**
     * SLF4J logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FbSlf4j.class);

    /**
     * Ctor.
     */
    public FbSlf4j() {
        super(
            req -> {
                FbSlf4j.log(req);
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
        FbSlf4j.LOGGER.error(
            "{} {} failed with {}: {}",
            new RqMethod.Base(req).method(),
            new RqHref.Base(req).href(),
            req.code(),
            new TextOf(new BytesOf(req.throwable()))
        );
    }

}
