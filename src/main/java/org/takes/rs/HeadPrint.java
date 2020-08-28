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
import java.io.OutputStream;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import org.cactoos.Text;
import org.cactoos.io.WriterTo;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.Sticky;
import org.cactoos.text.Split;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.takes.Head;
import org.takes.Response;

/**
 * Response head decorator that can print an entire head response in HTTP
 * format.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 2.0
 */
public final class HeadPrint implements Head, Text {

    /**
     * Pattern for first line.
     */
    private static final Pattern FIRST = Pattern.compile(
        "HTTP/1\\.1 \\d{3} [a-zA-Z ]+"
    );

    /**
     * Pattern for all other lines in the head.
     */
    private static final Pattern OTHERS = Pattern.compile(
        "[a-zA-Z0-9\\-]+:\\p{Print}+"
    );

    /**
     * HTTP End Of Line.
     */
    private static final String EOL = "\r\n";

    /**
     * Bytes representation.
     */
    private final IoChecked<byte[]> bytes;

    /**
     * Ctor.
     * @param res Original response
     */
    public HeadPrint(final Response res) {
        this.bytes = new IoChecked<>(
            new Sticky<>(
                () -> {
                    final ByteArrayOutputStream baos =
                        new ByteArrayOutputStream();
                    final Writer writer = new WriterTo(baos);
                    int pos = 0;
                    try {
                        for (final String line : res.head()) {
                            if (pos == 0 && !HeadPrint.FIRST.matcher(line)
                                .matches()) {
                                throw new IllegalArgumentException(
                                    String.format(
                                        // @checkstyle LineLength (1 line)
                                        "first line of HTTP response \"%s\" doesn't match \"%s\" regular expression, but it should, according to RFC 7230",
                                        line, HeadPrint.FIRST
                                    )
                                );
                            }
                            if (pos > 0 && !HeadPrint.OTHERS.matcher(line)
                                .matches()) {
                                throw new IllegalArgumentException(
                                    String.format(
                                        // @checkstyle LineLength (1 line)
                                        "header line #%d of HTTP response \"%s\" doesn't match \"%s\" regular expression, but it should, according to RFC 7230",
                                        pos + 1, line, HeadPrint.OTHERS
                                    )
                                );
                            }
                            writer.append(line);
                            writer.append(HeadPrint.EOL);
                            ++pos;
                        }
                        writer.append(HeadPrint.EOL);
                    } finally {
                        writer.flush();
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
    public Iterable<String> head() throws IOException {
        final Iterable<Text> lines = new Split(
            HeadPrint.EOL,
            new TextOf(this.bytes.value())
        );
        final List<String> result = new LinkedList<>();
        for (final Text line : lines) {
            result.add(new UncheckedText(line).asString());
        }
        return result;
    }

}
