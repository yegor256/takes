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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.Text;
import org.cactoos.text.Joined;
import org.cactoos.text.TextOf;
import org.takes.Response;

/**
 * Response decorator that can print an entire response in HTTP format.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 * @todo #1054:30min Continue replacing guava in tests.
 *  Create new todos until guava is removed and Takes is much more Cactoos
 *  oriented as started with #804.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsPrint extends RsWrap implements Text {

    /**
     * Head print representation.
     */
    private final HeadPrint head;

    /**
     * Body print representation.
     */
    private final BodyPrint body;

    /**
     * Ctor.
     * @param res Original response
     */
    public RsPrint(final Response res) {
        super(res);
        this.head = new HeadPrint(res);
        this.body = new BodyPrint(res);
    }

    @Override
    public String asString() throws IOException {
        return new Joined(
            new TextOf(""),
            this.head,
            this.body
        ).asString();
    }

    /**
     * Print body into string.
     * @return Entire body of HTTP response
     * @throws IOException If fails
     */
    public String printBody() throws IOException {
        return this.body.asString();
    }

    /**
     * Print head into string.
     * @return Entire head of HTTP response
     * @throws IOException If fails
     * @since 0.10
     */
    public String printHead() throws IOException {
        return this.head.asString();
    }

    /**
     * Print it into output stream.
     * @param output Output to print into
     * @throws IOException If fails
     * @todo #1054:30min Remove the #print(OutputStream) methods.
     *  After the creation of {@link HeadPrint} and {@link BodyPrint} classes,
     *  these methods lost sense and need be removed from these classes and
     *  all tests that uses #print() method.
     */
    public void print(final OutputStream output) throws IOException {
        this.head.print(output);
        this.body.print(output);
    }

    /**
     * Print it into output stream in UTF8.
     * @param output Output to print into
     * @throws IOException If fails
     * @since 0.10
     */
    public void printHead(final OutputStream output) throws IOException {
        this.head.print(output);
    }

    /**
     * Print it into a writer.
     * @param writer Writer to print into
     * @throws IOException If fails
     * @since 2.0
     */
    public void printHead(final Writer writer) throws IOException {
        try {
            writer.write(this.head.asString());
        } finally {
            writer.flush();
        }
    }

    /**
     * Print it into output stream.
     * @param output Output to print into
     * @throws IOException If fails
     */
    public void printBody(final OutputStream output) throws IOException {
        this.body.print(output);
    }

}
