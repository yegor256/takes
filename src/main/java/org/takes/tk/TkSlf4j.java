/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Yegor Bugayenko
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
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
@ToString(of = { "origin", "target" })
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
        final long start = System.currentTimeMillis();
        TkPatternLog log = new TkRqPatternLog(this.target, req, start);
        try {
            final Response rsp = this.origin.act(req);
            log.append("returned \"{}\"", rsp.head().iterator().next());
            return rsp;
        } catch (final IOException ex) {
            log.append("thrown {}(\"{}\")",
                            ex.getClass().getCanonicalName(),
                            ex.getLocalizedMessage());
            throw ex;
            // @checkstyle IllegalCatchCheck (1 line)
        } catch (final RuntimeException ex) {
            log.append("thrown runtime {}(\"{}\")",
                            ex.getClass().getCanonicalName(),
                            ex.getLocalizedMessage());
            throw ex;
        }
    }

    private interface TkPatternLog {
        void append(String pattern, Object... objects);
    }

    private static class TkRqPatternLog implements TkPatternLog {
        private final Logger logger;
        private final String rqMethod;
        private final String rqHref;
        private final long ms;

        public TkRqPatternLog(String target, Request request, long start) throws IOException {
            this.logger = LoggerFactory.getLogger(target);
            this.rqMethod = new RqMethod.Base(request).method();
            this.rqHref = new RqHref.Base(request).href().toString();
            this.ms = System.currentTimeMillis() - start;
        }

        @Override
        public void append(String pattern, Object... objects) {
            if (logger.isInfoEnabled()) {
                final List<Object> objectList = new ArrayList<>(2 + objects.length + 1);
                objectList.add(this.rqMethod);
                objectList.add(this.rqHref);
                objectList.addAll(Arrays.asList(objects));
                objectList.add(this.ms);
                logger.info("[{} {}] " + pattern + " in {} ms", objectList.toArray());
            }
        }
    }
}
