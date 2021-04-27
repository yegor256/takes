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
import org.cactoos.Text;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Joined;
import org.takes.Head;

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
     * HTTP End Of Line.
     */
    private static final String EOL = "\r\n";

    /**
     * The HTTP Response.
     */
    private final Head origin;

    /**
     * Ctor.
     * @param head Original head
     */
    public HeadPrint(final Head head) {
        this.origin = head;
    }

    @Override
    public String asString() throws IOException {
        return new FormattedText(
            "%s%s%s",
            new Joined(
                HeadPrint.EOL,
                this.head()
            ),
            HeadPrint.EOL,
            HeadPrint.EOL
        ).toString();
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.origin.head();
    }

}
