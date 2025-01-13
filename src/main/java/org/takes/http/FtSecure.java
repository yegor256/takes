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
import javax.net.ssl.SSLServerSocketFactory;
import lombok.EqualsAndHashCode;
import org.takes.Take;

/**
 * Secure (SSL) front.
 *
 * <p>Make sure that valid keystore and certificates are available
 * to the underlying JSSE infrastructure.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.25
 */
@EqualsAndHashCode
public final class FtSecure implements Front {

    /**
     * The original front that is initialized with an SSLServerSocket.
     */
    private final Front front;

    /**
     * Ctor.
     * @param tks Take
     * @throws IOException If fails
     */
    public FtSecure(final Take tks) throws IOException {
        this(tks, 443);
    }

    /**
     * Ctor.
     * @param tks Take
     * @param prt Port
     * @throws IOException If fails
     */
    public FtSecure(final Take tks, final int prt) throws IOException {
        this(new BkBasic(tks), prt);
    }

    /**
     * Ctor.
     * @param bck Back
     * @param port Port
     * @throws IOException If fails
     */
    public FtSecure(final Back bck, final int port) throws IOException {
        this(
            bck,
            SSLServerSocketFactory.getDefault().createServerSocket(port)
        );
    }

    /**
     * Ctor.
     * @param bck Back
     * @param skt Server socket
     */
    FtSecure(final Back bck, final ServerSocket skt) {
        this.front = new FtBasic(bck, skt);
    }

    @Override
    public void start(final Exit exit) throws IOException {
        this.front.start(exit);
    }
}
