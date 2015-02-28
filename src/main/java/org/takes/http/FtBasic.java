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
import java.net.SocketTimeoutException;
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
@EqualsAndHashCode(of = { "back", "port" })
public final class FtBasic implements Front {

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
     * @param prt Port
     */
    public FtBasic(final Takes tks, final int prt) {
        this(new BkBasic(tks), prt);
    }

    /**
     * Ctor.
     * @param bck Back
     * @param prt Port
     */
    public FtBasic(final Back bck, final int prt) {
        this.back = bck;
        this.port = prt;
    }

    @Override
    public void start(final Exit exit) throws IOException {
        final ServerSocket server = new ServerSocket(this.port);
        server.setSoTimeout((int) TimeUnit.SECONDS.toMillis(1L));
        try {
            while (!exit.ready()) {
                this.loop(server);
            }
        } finally {
            server.close();
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
        } catch (final SocketTimeoutException ex) {
            assert ex != null;
        }
    }

}
