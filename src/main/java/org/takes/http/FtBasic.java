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
package org.takes.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import org.takes.Take;

/**
 * Basic front.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode
public final class FtBasic implements Front {

    /**
     * Back.
     */
    private final Back back;

    /**
     * Server socket.
     */
    private final ServerSocket socket;

    /**
     * Ctor.
     * @param tks Take
     * @throws IOException If fails
     * @since 0.24
     */
    public FtBasic(final Take tks) throws IOException {
        this(new BkSafe(new BkBasic(tks)), 80);
    }

    /**
     * Ctor.
     * @param tks Take
     * @param prt Port
     * @throws IOException If fails
     */
    public FtBasic(final Take tks, final int prt) throws IOException {
        this(new BkSafe(new BkBasic(tks)), prt);
    }

    /**
     * Ctor.
     * @param bck Back
     * @param port Port
     * @throws IOException If fails
     */
    public FtBasic(final Back bck, final int port) throws IOException {
        this(bck, new ServerSocket(port));
    }

    /**
     * Ctor.
     * @param bck Back
     * @param skt Server socket
     * @since 0.22
     */
    public FtBasic(final Back bck, final ServerSocket skt) {
        this.back = bck;
        this.socket = skt;
    }

    @Override
    public void start(final Exit exit) throws IOException {
        this.socket.setSoTimeout((int) TimeUnit.SECONDS.toMillis(1L));
        try {
            do {
                this.loop(this.socket);
            } while (!exit.ready());
        } finally {
            this.socket.close();
        }
    }

    /**
     * Make a loop cycle.
     * @param server Server socket
     * @throws IOException If fails
     */
    private void loop(final ServerSocket server) throws IOException {
        try {
            this.back.accept(server.accept());
        } catch (final SocketTimeoutException ignored) {
        }
    }

}
