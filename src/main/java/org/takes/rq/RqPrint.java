/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import lombok.EqualsAndHashCode;
import org.cactoos.Text;
import org.cactoos.io.OutputTo;
import org.cactoos.io.TeeInput;
import org.cactoos.io.WriterTo;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.LengthOf;
import org.cactoos.text.Sticky;
import org.cactoos.text.TextOf;
import org.takes.Request;

/**
 * Request decorator, to print it all.
 *
 * <p>The class is immutable and thread-safe.
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class RqPrint extends RqWrap implements Text {

    /**
     * The textual representation.
     */
    private final Text text;

    /**
     * Ctor.
     * @param req Original request
     * @checkstyle AnonInnerLengthCheck (30 lines)
     */
    public RqPrint(final Request req) {
        super(req);
        this.text = new Sticky(
            new TextOf(
                () -> {
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    this.printHead(baos);
                    this.printBody(baos);
                    return new TextOf(baos.toByteArray()).asString();
                }
            )
        );
    }

    /**
     * Print it all.
     * @return Text form of request
     * @throws IOException If fails
     */
    public String print() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            this.print(baos);
            return new TextOf(baos.toByteArray()).toString();
        }
    }

    /**
     * Print it all.
     * @param output Output stream
     * @throws IOException If fails
     */
    public void print(final OutputStream output) throws IOException {
        new IoChecked<>(
            new LengthOf(new TeeInput(this.text, new OutputTo(output)))
        ).value();
    }

    /**
     * Print it all.
     * @return Text form of request
     * @throws IOException If fails
     */
    public String printHead() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.printHead(baos);
        return new TextOf(baos.toByteArray()).toString();
    }

    /**
     * Print it all.
     * @param output Output stream
     * @throws IOException If fails
     */
    public void printHead(final OutputStream output) throws IOException {
        final String eol = "\r\n";
        try (Writer writer = new WriterTo(output)) {
            for (final String line : this.head()) {
                writer.append(line);
                writer.append(eol);
            }
            writer.append(eol);
            writer.flush();
        }
    }

    /**
     * Print body.
     * @return Text form of request
     * @throws IOException If fails
     */
    public String printBody() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.printBody(baos);
        return new TextOf(baos.toByteArray()).toString();
    }

    /**
     * Print body.
     * @param output Output stream to print to
     * @throws IOException If fails
     */
    public void printBody(final OutputStream output) throws IOException {
        final InputStream input = new RqChunk(new RqLengthAware(this)).body();
        final byte[] buf = new byte[4096];
        while (true) {
            final int bytes = input.read(buf);
            if (bytes < 0) {
                break;
            }
            output.write(buf, 0, bytes);
        }
    }

    @Override
    public String asString() throws Exception {
        return this.text.asString();
    }
}
