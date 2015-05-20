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
import java.io.InputStream;
import org.takes.misc.Opt;

/**
 * InputStream witch can peek a value.
 * @author Lautaro Cozzani (lautaromail@gmail.com)
 * @version $Id$
 */
final class PeekInputStream extends InputStream {

    /**
     * Original Stream.
     */
    private final transient InputStream stream;

    /**
     * Peeked value.
     */
    private transient Opt<Integer> peeked;

    /**
     * Constructor.
     * @param input InputStream
     */
    public PeekInputStream(final InputStream input) {
        super();
        this.stream = input;
        this.peeked = new Opt.Empty<Integer>();
    }

    @Override
    public int read() throws IOException {
        final int ret;
        if (this.peeked.has()) {
            ret = this.peeked.get();
            this.peeked = new Opt.Empty<Integer>();
        } else {
            ret = this.stream.read();
        }
        return ret;
    }

    /**
     * Peek next int.
     * @return Next int
     * @throws IOException If read() fails
     */
    public int peek() throws IOException {
        if (!this.peeked.has()) {
            this.peeked = new Opt.Single<Integer>(this.stream.read());
        }
        return this.peeked.get();
    }
}
