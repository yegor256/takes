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
import java.nio.charset.StandardCharsets;
import org.cactoos.Text;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Joined;
import org.takes.Head;
import org.takes.Response;

/**
 * Response head decorator that can print an entire head response in HTTP
 * format.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 2.0
 * @todo #1054:30min Develop tests for {@link HeadPrint} and {@link BodyPrint}.
 *  Although these classes are tested by {@link RsPrint} they need your own
 *  unit tests.
 */
public final class HeadPrint implements Head, Text {

    /**
     * HTTP End Of Line.
     */
    private static final String EOL = "\r\n";

    /**
     * The HTTP Response.
     */
    private final Response response;

    /**
     * Ctor.
     * @param res Original response
     */
    public HeadPrint(final Response res) {
        this.response = res;
    }

    /**
     * Print it into output stream in UTF8.
     *
     * @param output Output to print into
     * @throws IOException If fails
     */
    public void print(final OutputStream output) throws IOException {
        try {
            output.write(this.asString().getBytes(StandardCharsets.UTF_8));
        } finally {
            output.flush();
        }
    }

    @Override
    public String asString() throws IOException {
        return new FormattedText(
            "%s%s%s",
            new Joined(
                HeadPrint.EOL,
                this.response.head()
            ),
            HeadPrint.EOL,
            HeadPrint.EOL
        ).asString();
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.response.head();
    }

}
