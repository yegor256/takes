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
package org.takes.rq;

import java.io.IOException;
import java.net.InetAddress;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request decorator to get custom socket headers.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Igor Khvostenkov (ikhvostenkov@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
public final class RqSocket extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     */
    public RqSocket(final Request req) {
        super(req);
    }

    /**
     * Returns IP address from the X-Takes-LocalAddress header.
     * @return Local InetAddress
     * @throws IOException If fails
     */
    public InetAddress getLocalAddress() throws IOException {
        return InetAddress.getByName(this.headerValue("X-Takes-LocalAddress"));
    }

    /**
     * Returns IP address from the X-Takes-RemoteAddress header.
     * @return Remote InetAddress
     * @throws IOException If fails
     */
    public InetAddress getRemoteAddress() throws IOException {
        return InetAddress.getByName(this.headerValue("X-Takes-RemoteAddress"));
    }

    /**
     * Returns port from the X-Takes-LocalPort header.
     * @return Local Port
     * @throws IOException If fails
     */
    public int getLocalPort() throws IOException {
        return this.getPort("X-Takes-LocalPort");
    }

    /**
     * Returns port from the X-Takes-RemotePort header.
     * @return Remote Port
     * @throws IOException If fails
     */
    public int getRemotePort() throws IOException {
        return this.getPort("X-Takes-RemotePort");
    }

    /**
     * Parses value of the provided header from head.
     * @param header Header to parse
     * @return String value of the provided header
     * @throws IOException If fails
     */
    private String headerValue(final CharSequence header) throws IOException {
        String result = "";
        for (final String line : this.head()) {
            final String[] parts = line.split(": ", 2);
            if (parts[0].contains(header)) {
                result = parts[1];
            }
        }
        return result;
    }

    /**
     * Parses port value from the provided header.
     * @param header Header to parse
     * @return Integer value of the port
     * @throws IOException If fails
     */
    private int getPort(final CharSequence header) throws IOException {
        return Integer.parseInt(this.headerValue(header));
    }
}
