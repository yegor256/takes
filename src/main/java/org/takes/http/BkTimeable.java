/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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
package org.takes.http;

import java.io.IOException;
import java.net.Socket;
import lombok.EqualsAndHashCode;

/**
 * Back decorator with maximum lifetime.
 * <p>The class is immutable and thread-safe.
 * @author Aleksey Kurochka (eg04lt3r@gmail.com)
 * @version $Id$
 */
@EqualsAndHashCode(of = { "origin", "latency" })
public final class BkTimeable implements Back {

    /**
     * Origin back.
     */
    private final transient Back origin;

    /**
     * Maximum latency in milliseconds.
     */
    private final transient long latency;

    /**
     * Ctor.
     * @param back Original back
     * @param ltc Maximum latency
     */
    public BkTimeable(final Back back, final long ltc) {
        this.origin = back;
        this.latency = ltc;
    }

    @Override
    @SuppressWarnings("PMD.DoNotUseThreads")
    public void accept(final Socket socket) throws IOException {
        this.origin.accept(socket);
        final Thread callerThread = Thread.currentThread();
        final Thread monitor = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(BkTimeable.this.latency);
                        callerThread.interrupt();
                    } catch (final InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException(ex);
                    }
                }
            }
        );
        monitor.start();
    }
}
