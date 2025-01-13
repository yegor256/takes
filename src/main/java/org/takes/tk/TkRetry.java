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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.ListOf;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;

/**
 * Decorator TkRetry, which will not fail immediately on Exception, but
 * will retry a few times.
 *
 * @since 0.28.3
 */
public final class TkRetry implements Take {

    /**
     * Maximum number of retry attempts.
     */
    private final int count;

    /**
     * Amount of time between retries, in milliseconds.
     */
    private final int delay;

    /**
     * Original Take.
     */
    private final Take take;

    /**
     * Constructor.
     *
     * @param retries Number of retry attempts
     * @param wait Time between retries
     * @param original Original take
     */
    public TkRetry(final int retries, final int wait, final Take original) {
        this.count = retries;
        this.delay = wait;
        this.take = original;
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public Response act(final Request req) throws IOException {
        if (this.count <= 0) {
            throw new IllegalArgumentException(
                "Can't make less than one attempt"
            );
        }
        int attempts = 0;
        final List<Exception> failures = new ArrayList<>(this.count);
        while (attempts < this.count) {
            try {
                return this.take.act(req);
                //@checkstyle IllegalCatch (1 line)
            } catch (final Exception ex) {
                ++attempts;
                failures.add(ex);
                this.sleep();
            }
        }
        throw new IOException(
            String.format(
                "failed after %d attempts: %s",
                failures.size(),
                new ListOf<>(
                    new Mapped<>(
                        Exception::getMessage,
                        failures
                    )
                )
            ),
            failures.get(failures.size() - 1)
        );
    }

    /**
     * Sleep.
     */
    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(this.delay);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                "Unexpected interruption while retrying to process request",
                ex
            );
        }
    }

}
