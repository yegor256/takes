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
package org.takes.rq;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request decorator, to prevent multiple calls to {@code body()} method.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.36
 * @todo #918:30min Please use {@link org.cactoos.scalar.Sticky} decorator
 *  class so that multiple calls to body() would produce the same cached result
 *  rather than throwing an exception.
 */
@EqualsAndHashCode(callSuper = true)
public final class RqOnce extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     */
    public RqOnce(final Request req) {
        super(RqOnce.wrap(req));
    }

    /**
     * Wrap the request.
     * @param req Request
     * @return New request
     */
    private static Request wrap(final Request req) {
        final AtomicBoolean seen = new AtomicBoolean(false);
        return new RequestOf(
            req::head,
            () -> {
                if (!seen.getAndSet(true)) {
                    throw new IllegalStateException(
                        "It's not allowed to call body() more than once"
                    );
                }
                return req.body();
            }
        );
    }
}
