/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
