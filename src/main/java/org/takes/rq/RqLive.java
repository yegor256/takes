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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Live request.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class RqLive extends RqWrap {

    /**
     * Ctor.
     * @param input Input stream
     * @throws IOException If fails
     */
    public RqLive(final InputStream input) throws IOException {
        super(RqLive.parse(input));
    }

    /**
     * Parse input stream.
     * @param input Input stream
     * @return Request
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private static Request parse(final InputStream input) throws IOException {
        final Collection<String> head = new LinkedList<String>();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (true) {
            final int data = input.read();
            if (data < 0) {
                break;
            }
            if (data == '\r') {
                if (input.read() != '\n') {
                    throw new IOException("");
                }
                if (baos.size() == 0) {
                    break;
                }
                head.add(new String(baos.toByteArray()));
                baos.reset();
            } else {
                baos.write(data);
            }
        }
        return new Request() {
            @Override
            public Iterable<String> head() {
                return head;
            }
            @Override
            public InputStream body() {
                return input;
            }
        };
    }

}
