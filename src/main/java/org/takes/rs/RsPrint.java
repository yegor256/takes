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
package org.takes.rs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.takes.Response;

/**
 * Response decorator that can print an entire response in HTTP format.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = "origin")
public final class RsPrint implements Response {

    /**
     * Original response.
     */
    private final transient Response origin;

    /**
     * Default encoding.
     */
    private final transient String encoding;

    /**
     * Ctor.
     * @param res Original response
     */
    public RsPrint(final Response res) {
        this(res, "UTF-8");
    }

    /**
     * Ctor.
     * @param res Original response
     * @param enc Default encoding
     */
    public RsPrint(final Response res, final String enc) {
        this.origin = res;
        this.encoding = enc;
    }

    /**
     * Print it into string.
     * @return Entire HTTP response
     * @throws IOException If fails
     */
    public String print() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.print(baos);
        return new String(baos.toByteArray(), this.encoding);
    }

    /**
     * Print it into output stream.
     * @param output Output to print into
     * @throws IOException If fails
     */
    public void print(final OutputStream output) throws IOException {
        final InputStream body = this.body();
        final Writer writer = new OutputStreamWriter(output, this.encoding);
        try {
            final String eol = "\r\n";
            for (final String line : this.head()) {
                writer.append(line);
                writer.append(eol);
            }
            writer.append(eol);
            writer.flush();
            while (true) {
                final int data = body.read();
                if (data < 0) {
                    break;
                }
                output.write(data);
            }
        } finally {
            body.close();
        }
    }

    @Override
    public List<String> head() {
        return this.origin.head();
    }

    @Override
    public InputStream body() {
        return this.origin.body();
    }
}
