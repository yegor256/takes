/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.tk;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.Func;
import org.cactoos.Scalar;
import org.cactoos.map.MapEntry;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Joined;
import org.cactoos.text.TextOf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rq.RqMethod;

/**
 * Logs Take.act() calls.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.11.2
 */
@ToString(of = {"origin", "target"})
@EqualsAndHashCode
public final class TkSlf4j implements Take {

    /**
     * Original take.
     */
    private final Take origin;

    /**
     * Log target.
     */
    private final String target;

    /**
     * Ctor.
     * @param take Original
     */
    public TkSlf4j(final Take take) {
        this(take, TkSlf4j.class);
    }

    /**
     * Ctor.
     * @param take Original
     * @param tgt Log target
     */
    public TkSlf4j(final Take take, final Class<?> tgt) {
        this(take, tgt.getCanonicalName());
    }

    /**
     * Ctor.
     * @param take Original
     * @param tgt Log target
     */
    public TkSlf4j(final Take take, final String tgt) {
        this.target = tgt;
        this.origin = take;
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public Response act(final Request req) throws Exception {
        final Scalar<Long> time = System::currentTimeMillis;
        final long start = time.value();
        final Logger logger = LoggerFactory.getLogger(this.target);
        final Func<MapEntry<String, Object[]>, String> entry =
            params -> new Joined(
                new TextOf(" "),
                new FormattedText(
                    "[%s %s]",
                    new RqMethod.Base(req).method(),
                    new RqHref.Base(req).href()
                ),
                new FormattedText(params.getKey(), params.getValue()),
                new FormattedText("in %d ms", time.value() - start)
            ).asString();
        try {
            final Response rsp = this.origin.act(req);
            if (logger.isInfoEnabled()) {
                logger.info(
                    entry.apply(
                        new MapEntry<>(
                            "returned \"%s\"",
                            new Object[]{rsp.head().iterator().next()}
                        )
                    )
                );
            }
            return rsp;
        } catch (final IOException ex) {
            if (logger.isInfoEnabled()) {
                logger.info(
                    entry.apply(
                        new MapEntry<>(
                            "thrown %s(\"%s\")",
                            new Object[]{
                                ex.getClass().getCanonicalName(),
                                ex.getLocalizedMessage(),
                            }
                        )
                    )
                );
            }
            throw ex;
            // @checkstyle IllegalCatchCheck (1 line)
        } catch (final RuntimeException ex) {
            if (logger.isInfoEnabled()) {
                logger.info(
                    entry.apply(
                        new MapEntry<>(
                            "thrown runtime %s(\"%s\")",
                            new Object[]{
                                ex.getClass().getCanonicalName(),
                                ex.getLocalizedMessage(),
                            }
                        )
                    )
                );
            }
            throw ex;
        }
    }
}
