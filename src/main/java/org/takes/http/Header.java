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

/**
 * Http headers used in back-ends.
 *
 * @author Dan Baleanu (dan.baleanu@gmail.com)
 * @version $Id$
 * @since 0.31.2
 */
@SuppressWarnings("PMD.LongVariable")
public final class Header {

    /**
     * The http header "X-Takes-LocalAddress".
     */
    public static final Header X_TAKES_LOCAL_ADDRESS = new Header(
        "X-Takes-LocalAddress"
    );

    /**
     * The http header "X-Takes-LocalPort".
     */
    public static final Header X_TAKES_LOCAL_PORT = new Header(
        "X-Takes-LocalPort"
    );

    /**
     * The http header "X-Takes-RemoteAddress".
     */
    public static final Header X_TAKES_REMOTE_ADDRESS = new Header(
        "X-Takes-RemoteAddress"
    );

    /**
     * The http header "X-Takes-RemotePort".
     */
    public static final Header X_TAKES_REMOTE_PORT = new Header(
        "X-Takes-RemotePort"
    );

    /**
     * The header name.
     */
    private final transient String name;

    /**
     * Ctor.
     * @param name Header name.
     */
    private Header(final String name) {
        this.name = name;
    }

    /**
     * Returns the header name.
     * @return The header name.
     */
    @Override
    public String toString() {
        return this.name;
    }
}
