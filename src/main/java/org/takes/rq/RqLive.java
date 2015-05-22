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
import org.takes.misc.Opt;

/**
 * Live request.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 * @checkstyle CyclomaticComplexityCheck (500 lines)
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
        Opt<Integer> data = new Opt.Empty<Integer>();
        while (true) {
            data = data(input, data);
            if (data.get() < 0) {
                break;
            }
            if (data.get() == '\r') {
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
                data = new Opt.Single<Integer>(input.read());
                if (data.get() != ' ' && data.get() != '\t') {
                    head.add(new String(baos.toByteArray()));
                    baos.reset();
                }
                continue;
            }
            // @checkstyle MagicNumber (1 line)
            if ((data.get() > 0x7f || data.get() < 0x20)
                && data.get() != '\t') {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    String.format(
                        // @checkstyle LineLength (1 line)
                        "illegal character 0x%02X in HTTP header line #%d: \"%s\"",
                        data.get(),
                        head.size() + 1,
                        new String(baos.toByteArray())
                    )
                );
            }
            baos.write(data.get());
            data = new Opt.Empty<Integer>();
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
     * Obtains new byte if hasn't.
     * @param input Stream
     * @param data Empty or current data
     * @return Next or current data
     * @throws IOException if input.read() fails
     */
    private static Opt<Integer> data(final InputStream input,
            final Opt<Integer> data) throws IOException {
        final Opt<Integer> ret;
        if (data.has()) {
            ret = data;
        } else {
            ret = new Opt.Single<Integer>(input.read());
        }
        return ret;
    }
}
