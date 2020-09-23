/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Yegor Bugayenko
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
import org.cactoos.Text;
import org.cactoos.text.TextOf;
import org.takes.Response;

/**
 * Response body decorator that can print an entire body response in HTTP
 * format.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 2.0
 */
public final class BodyPrint implements Body, Text {

    /**
     * The HTTP Response.
     */
    private final Response response;

    /**
     * Ctor.
     *
     * @param res Original response
     */
    public BodyPrint(final Response res) {
        this.response = res;
    }

    /**
     * Print it into output stream in UTF8.
     *
     * @param output Output to print into
     * @throws IOException If fails
     */
    public void print(final OutputStream output) throws IOException {
        // @checkstyle MagicNumber (1 line)
        final byte[] buf = new byte[4096];
        final InputStream body = this.response.body();
        try {
            while (true) {
                final int bts = body.read(buf);
                if (bts < 0) {
                    break;
                }
                output.write(buf, 0, bts);
            }
        } finally {
            output.flush();
        }
    }

    @Override
    public String asString() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.print(baos);
        return new TextOf(baos.toByteArray()).asString();
    }

    @Override
    public InputStream stream() throws IOException {
        return this.response.body();
    }

    @Override
    public int length() throws IOException {
        return this.asString().length();
    }
}
