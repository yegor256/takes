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
import java.net.ServerSocket;
import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Random TCP ports.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.14
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class Ports {

    /**
     * Already assigned ports.
     */
    private static final Collection<Integer> ASSIGNED =
        new ConcurrentSkipListSet<Integer>();

    /**
     * Allocate a new random TCP port.
     * @return TCP port
     * @throws IOException If fails
     */
    public int allocate() throws IOException {
        synchronized (Ports.ASSIGNED) {
            int attempts = 0;
            int prt;
            do {
                prt = this.random();
                ++attempts;
                // @checkstyle MagicNumber (1 line)
                if (attempts > 100) {
                    throw new IllegalStateException(
                        String.format(
                            "failed to allocate TCP port after %d attempts",
                            attempts
                        )
                    );
                }
            } while (Ports.ASSIGNED.contains(prt));
            return prt;
        }
    }

    /**
     * Release it.
     * @param port Port
     */
    public void release(final int port) {
        Ports.ASSIGNED.remove(port);
    }

    /**
     * Allocate a new random TCP port.
     * @return TCP port
     * @throws IOException If fails
     */
    private int random() throws IOException {
        final ServerSocket socket = new ServerSocket(0);
        try {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } finally {
            socket.close();
        }
    }

}
