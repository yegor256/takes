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
 * Back decorator with lifetime.
 * <p>The class is immutable and thread-safe.
 * @author Aleksey Kurochka (eg04lt3r@gmail.com)
 * @version $Id$
 */
@EqualsAndHashCode(of = {"origin", "latency", "start"})
public final class BkTimeable implements Back {

    /**
     * Origin back.
     */
    private final transient Back origin;

    /**
     * Latency in milliseconds.
     */
    private final transient long latency;

    /**
     * Start time of instance creation.
     */
    private final transient long start;

    /**
     * Ctor.
     * @param back Original back
     * @param latency Latency delay
     */
    public BkTimeable(Back back, long latency) {
        this.origin = back;
        this.latency = latency;
        this.start = System.currentTimeMillis();
    }

    @Override
    public void accept(Socket socket) throws IOException {
        if (!latencyExceeded()) {
            this.origin.accept(socket);
        } else {
            Thread.currentThread().interrupt();
        }
    }

    private boolean latencyExceeded() {
        return System.currentTimeMillis() - start > latency;
    }
}
