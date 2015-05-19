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
package org.takes.rq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.takes.HttpException;
import org.takes.Request;

/**
 * Live request.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("PMD.CyclomaticComplexity")
public final class RqLive extends RqWrap {

    /**
     * Ctor.
     * @param input Input stream
     * @throws IOException If fails
     */
    public RqLive(final InputStream input) throws IOException {
        super(RqLive.parse(input));
    }

    /**
     * Parse input stream.
     * @param input Input stream
     * @return Request
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private static Request parse(final InputStream input) throws IOException {
        final List<String> head = new LinkedList<String>();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (true) {
            final int data = input.read();
            if (data < 0) {
                break;
            }
            if (data == '\r') {
                if (input.read() != '\n') {
                    throw new HttpException(
                        HttpURLConnection.HTTP_BAD_REQUEST,
                        String.format(
                            // @checkstyle LineLength (1 line)
                            "there is no LF after CR in header, line #%d: \"%s\"",
                            head.size() + 1, new String(baos.toByteArray())
                        )
                    );
                }
                if (baos.size() == 0) {
                    break;
                }
                checkAndAddHeader(head, new String(baos.toByteArray()));
                baos.reset();
                continue;
            }
            // @checkstyle MagicNumber (1 line)
            if ((data > 0x7f || data < 0x20) && data != '\t') {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    String.format(
                        // @checkstyle LineLength (1 line)
                        "illegal character 0x%02X in HTTP header line #%d: \"%s\"",
                        data, head.size() + 1, new String(baos.toByteArray())
                    )
                );
            }
            baos.write(data);
        }
        return new Request() {
            @Override
            public Iterable<String> head() {
                return head;
            }
            @Override
            public InputStream body() {
                return input;
            }
        };
    }

    /**
     * Check line and add header o replace last one.
     * @param head All previous headers
     * @param header Current header
     * @throws HttpException if multiline do not start with space
     */
    private static void checkAndAddHeader(
        final List<String> head,
        final String header
    ) throws HttpException {
        if (head.isEmpty() || header.contains(":")) {
            head.add(header);
        } else if (header.charAt(0) == ' ' || header.charAt(0) == '\t') {
            head.add(
                String.format("%s%s", head.remove(head.size() - 1), header)
            );
        } else {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format(
                    // @checkstyle LineLength (1 line)
                    "there is no ':' character in header, line #%d: \"%s\"",
                    head.size() + 1, header
                )
            );
        }
    }

}
