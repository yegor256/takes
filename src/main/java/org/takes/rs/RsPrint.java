/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.Text;
import org.takes.Response;

/**
 * Response decorator that can print an entire response in HTTP format.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsPrint extends RsWrap implements Text {

    /**
     * Pattern for first line.
     */
    private static final Pattern FIRST = Pattern.compile(
        "HTTP/1\\.1 \\d{3} [a-zA-Z- ]+"
    );

    /**
     * Pattern for all other lines in the head.
     */
    private static final Pattern OTHERS = Pattern.compile(
        "[a-zA-Z0-9\\-]+:\\p{Print}+"
    );

    /**
     * Ctor.
     * @param res Original response
     */
    public RsPrint(final Response res) {
        super(res);
    }

    @Override
    public String asString() throws Exception {
        return this.print();
    }

    /**
     * Print it into string.
     * @return Entire HTTP response
     * @throws IOException If fails
     */
    public String print() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.print(baos);
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Print body into string.
     * @return Entire body of HTTP response
     * @throws IOException If fails
     */
    public String printBody() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.printBody(baos);
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Print head into string.
     * @return Entire head of HTTP response
     * @throws IOException If fails
     * @since 0.10
     */
    public String printHead() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.printHead(baos);
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Print it into output stream.
     * @param output Output to print into
     * @throws IOException If fails
     */
    public void print(final OutputStream output) throws IOException {
        this.printHead(output);
        this.printBody(output);
    }

    /**
     * Print it into output stream.
     * @param output Output to print into
     * @throws IOException If fails
     * @since 0.10
     */
    public void printHead(final OutputStream output) throws IOException {
        final String eol = "\r\n";
        final Writer writer =
            new OutputStreamWriter(output, StandardCharsets.UTF_8);
        int pos = 0;
        for (final String line : this.head()) {
            if (pos == 0 && !RsPrint.FIRST.matcher(line).matches()) {
                throw new IllegalArgumentException(
                    String.format(
                        "First line of HTTP response \"%s\" doesn't match \"%s\" regular expression, but it should, according to RFC 7230",
                        line, RsPrint.FIRST
                    )
                );
            }
            if (pos > 0 && !RsPrint.OTHERS.matcher(line).matches()) {
                throw new IllegalArgumentException(
                    String.format(
                        "Header line #%d of HTTP response \"%s\" doesn't match \"%s\" regular expression, but it should, according to RFC 7230",
                        pos + 1, line, RsPrint.OTHERS
                    )
                );
            }
            writer.append(line);
            writer.append(eol);
            ++pos;
        }
        writer.append(eol);
        writer.flush();
    }

    /**
     * Print it into output stream.
     * @param output Output to print into
     * @throws IOException If fails
     */
    public void printBody(final OutputStream output) throws IOException {
        final InputStream body = this.body();
        try {
            final byte[] buf = new byte[4096];
            while (true) {
                final int bytes = body.read(buf);
                if (bytes < 0) {
                    break;
                }
                output.write(buf, 0, bytes);
            }
        } finally {
            body.close();
        }
    }
}
