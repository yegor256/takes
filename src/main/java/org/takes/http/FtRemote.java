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
import java.net.URI;
import lombok.EqualsAndHashCode;

/**
 * Front remote control.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@SuppressWarnings("PMD.DoNotUseThreads")
@EqualsAndHashCode(of = "origin")
public final class FtRemote implements Front {

    /**
     * Origin.
     */
    private final transient Front origin;

    /**
     * Ctor.
     * @param front Front
     */
    public FtRemote(final Front front) {
        this.origin = front;
    }

    @Override
    public void listen(final int port) throws IOException {
        this.origin.listen(port);
    }

    /**
     * Execute this script against a running front.
     * @param script Script to run
     * @throws IOException If fails
     */
    public void exec(final FtRemote.Script script) throws IOException {
        final int port = FtRemote.port();
        final Thread thread = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    try {
                        FtRemote.this.listen(port);
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
        );
        thread.start();
        script.exec(URI.create(String.format("http://localhost:%d", port)));
        thread.interrupt();
    }

    /**
     * Reserve new TCP port.
     * @return Reserved port.
     * @throws IOException If fails
     */
    private static int port() throws IOException {
        final ServerSocket socket = new ServerSocket(0);
        try {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } finally {
            socket.close();
        }
    }

    /**
     * Script to execute.
     */
    public interface Script {
        /**
         * Execute it against this URI.
         * @param home URI of the running front
         * @throws IOException If fails
         */
        void exec(URI home) throws IOException;
    }

}
