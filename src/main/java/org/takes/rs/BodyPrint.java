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
import org.cactoos.io.InputOf;
import org.cactoos.io.UncheckedInput;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.Sticky;
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
     * Bytes representation.
     */
    private final IoChecked<byte[]> bytes;

    /**
     * Ctor.
     * @param res Original response
     */
    public BodyPrint(final Response res) {
        this.bytes = new IoChecked<>(
            new Sticky<>(
                () -> {
                    final ByteArrayOutputStream baos =
                        new ByteArrayOutputStream();
                    //@checkstyle MagicNumberCheck (1 line)
                    final byte[] buf = new byte[4096];
                    try (InputStream body = res.body()) {
                        while (true) {
                            final int bts = body.read(buf);
                            if (bts < 0) {
                                break;
                            }
                            baos.write(buf, 0, bts);
                        }
                    } finally {
                        baos.flush();
                    }
                    return baos.toByteArray();
                }
            )
        );
    }

    /**
     * Print it into output stream in UTF8.
     *
     * @param output Output to print into
     * @throws IOException If fails
     */
    public void print(final OutputStream output) throws IOException {
        try {
            output.write(this.bytes.value());
        } finally {
            output.flush();
        }
    }

    @Override
    public String asString() throws IOException {
        return new TextOf(this.bytes.value()).asString();
    }

    @Override
    public InputStream stream() throws IOException {
        return new UncheckedInput(new InputOf(this.bytes.value())).stream();
    }

    @Override
    public int length() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.print(baos);
        return baos.size();
    }

}
