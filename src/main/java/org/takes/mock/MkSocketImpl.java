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
package org.takes.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;

/**
 * Mock/fake socket impl.
 *
 * @author Piotr Pradzynski (prondzyn@gmail.com)
 * @version $Id$
 * @since 0.26
 */
final class MkSocketImpl extends SocketImpl {

    /**
     * Input stream.
     */
    private final transient InputStream input;
    /**
     * Output stream.
     */
    private final transient OutputStream output;

    /**
     * Ctor.
     * @param ins Input stream
     * @param outs Output stream
     */
    MkSocketImpl(final InputStream ins, final OutputStream outs) {
        super();
        this.input = ins;
        this.output = outs;
    }

    @Override
    public void setOption(final int oid, final Object value)
        throws SocketException {
        //body skipped
    }

    @Override
    public Object getOption(final int oid) throws SocketException {
        return null;
    }

    @Override
    public void create(final boolean stream) throws IOException {
        //body skipped
    }

    @Override
    public void connect(final String host,
        final int port) throws IOException {
        //body skipped
    }

    @Override
    public void connect(final InetAddress address,
        final int port) throws IOException {
        //body skipped
    }

    @Override
    public void connect(final SocketAddress address,
        final int timeout) throws IOException {
        //body skipped
    }

    @Override
    public void bind(final InetAddress host, final int port)
        throws IOException {
        //body skipped
    }

    @Override
    public void listen(final int backlog) throws IOException {
        //body skipped
    }

    @Override
    public void accept(final SocketImpl socket)
        throws IOException {
        //body skipped
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.input;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.output;
    }

    @Override
    public int available() throws IOException {
        return 0;
    }

    @Override
    public void close() throws IOException {
        //body skipped
    }

    @Override
    public void sendUrgentData(final int data)
        throws IOException {
        //body skipped
    }
}
