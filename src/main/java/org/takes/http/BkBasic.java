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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.takes.Response;
import org.takes.Takes;
import org.takes.rq.RqPlain;
import org.takes.rs.RsPrint;

/**
 * Basic back-end.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = "takes")
public final class BkBasic implements Back {

    /**
     * Takes.
     */
    private final transient Takes takes;

    /**
     * Ctor.
     * @param tks Takes
     */
    public BkBasic(final Takes tks) {
        this.takes = tks;
    }

    @Override
    public void accept(final Socket socket) throws IOException {
        final InputStream input = socket.getInputStream();
        final BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                new BufferedInputStream(input),
                "UTF-8"
            )
        );
        final List<String> head = new LinkedList<String>();
        while (true) {
            final String line = reader.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            head.add(line);
        }
        final Response response = this.takes.take(
            new RqPlain(head, input)
        ).print();
        final OutputStream output = socket.getOutputStream();
        try {
            new RsPrint(response).print(output);
        } finally {
            output.close();
        }
    }

}
