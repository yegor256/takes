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
package org.takes.misc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Converts supplied string to an input stream encoded as bytes in default
 * encoding.
 *
 * @author I. Sokolov (happy.neko@gmail.com)
 * @version $Id$
 */
public final class MockInputStream extends ByteArrayInputStream {
    /**
     * Closed stream flag.
     */
    private final AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * Instantiates MockInputStream based on supplied string. String is
     * converted to bytes stream using default charset encoding.
     * @param input Source String
     */
    public MockInputStream(final String input) {
        super(input.getBytes());
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.closed.set(true);
    }

    /**
     * Checks whether stream was closed.
     * @return True if stream was closed, otherwise false
     */
    public boolean isClosed() {
        return this.closed.get();
    }
}
