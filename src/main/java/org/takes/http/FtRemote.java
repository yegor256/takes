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
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.EqualsAndHashCode;
import org.takes.Takes;

/**
 * Front remote control.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.DoNotUseThreads")
@EqualsAndHashCode(of = { "back", "port" })
public final class FtRemote implements Front {

    /**
     * Already assigned ports.
     */
    private static final Set<Integer> PORTS =
        new ConcurrentSkipListSet<Integer>();

    /**
     * Back.
     */
    private final transient Back back;

    /**
     * Port.
     */
    private final transient int port;

    /**
     * Ctor.
     * @param tks Takes
     * @throws IOException If fails
     */
    public FtRemote(final Takes tks) throws IOException {
        this(new BkParallel(new BkBasic(tks)));
    }

    /**
     * Ctor.
     * @param bck Back
     * @throws IOException If fails
     */
    public FtRemote(final Back bck) throws IOException {
        this.back = bck;
        this.port = FtRemote.allocate();
    }

    @Override
    public void start(final Exit exit) throws IOException {
        new FtBasic(this.back, this.port).start(exit);
    }

    /**
     * Execute this script against a running front.
     * @param script Script to run
     * @throws IOException If fails
     */
    public void exec(final FtRemote.Script script) throws IOException {
        final AtomicBoolean exit = new AtomicBoolean();
        final Thread thread = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    try {
                        FtRemote.this.start(
                            new Exit() {
                                @Override
                                public boolean ready() {
                                    return exit.get();
                                }
                            }
                        );
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
        );
        thread.start();
        script.exec(
            URI.create(
                String.format("http://localhost:%d", this.port)
            )
        );
        exit.set(true);
        try {
            thread.join();
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
        FtRemote.PORTS.remove(this.port);
    }

    /**
     * Allocate a new random TCP port.
     * @return TCP port
     * @throws IOException If fails
     */
    private static int allocate() throws IOException {
        synchronized (FtRemote.PORTS) {
            int port;
            do {
                port = FtRemote.random();
            } while (FtRemote.PORTS.contains(port));
            return port;
        }
    }

    /**
     * Allocate a new random TCP port.
     * @return TCP port
     * @throws IOException If fails
     */
    private static int random() throws IOException {
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
