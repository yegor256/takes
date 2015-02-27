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
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import org.takes.Takes;

/**
 * Basic front.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@SuppressWarnings("PMD.DoNotUseThreads")
@EqualsAndHashCode(of = { "back", "threads" })
public final class FtBasic implements Front {

    /**
     * Back.
     */
    private final transient Back back;

    /**
     * Max threads.
     */
    private final transient int threads;

    /**
     * Ctor.
     * @param tks Takes
     */
    public FtBasic(final Takes tks) {
        this(new BkBasic(tks));
    }

    /**
     * Ctor.
     * @param bck Back
     */
    public FtBasic(final Back bck) {
        this(bck, 50);
    }

    /**
     * Ctor.
     * @param takes Takes
     * @param max Max threads
     */
    public FtBasic(final Takes takes, final int max) {
        this(new BkBasic(takes), max);
    }

    /**
     * Ctor.
     * @param bck Back
     * @param max Max threads
     */
    public FtBasic(final Back bck, final int max) {
        this.back = bck;
        this.threads = max;
    }

    @Override
    public void listen(final int port) throws IOException {
        final ScheduledExecutorService service =
            Executors.newScheduledThreadPool(this.threads);
        final ServerSocket server = new ServerSocket(port);
        final Runnable proc = new Runnable() {
            @Override
            public void run() {
                FtBasic.this.accept(server);
            }
        };
        for (int idx = 0; idx < this.threads; ++idx) {
            service.scheduleWithFixedDelay(proc, 1L, 1L, TimeUnit.NANOSECONDS);
        }
        try {
            TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        } finally {
            server.close();
        }
    }

    /**
     * Accept new connection.
     * @param server Server socket
     */
    private void accept(final ServerSocket server) {
        try {
            final Socket socket = server.accept();
            try {
                this.back.accept(socket);
            } finally {
                socket.close();
            }
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
