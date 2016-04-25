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
import java.util.Collection;
import java.util.LinkedList;
import lombok.EqualsAndHashCode;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.misc.Opt;
import org.takes.misc.UTF8String;

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
    @SuppressWarnings
        (
            {
                "PMD.AvoidInstantiatingObjectsInLoops",
                "PMD.StdCyclomaticComplexity",
                "PMD.ModifiedCyclomaticComplexity"
            }
        )
    private static Request parse(final InputStream input) throws IOException {
        boolean eof = true;
        final Collection<String> head = new LinkedList<String>();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Opt<Integer> data = new Opt.Empty<Integer>();
        while (true) {
            data = RqLive.data(input, data);
            if (data.get() < 0) {
                break;
            }
            eof = false;
            if (data.get() == '\r') {
                if (input.read() != '\n') {
                    throw new HttpException(
                        HttpURLConnection.HTTP_BAD_REQUEST,
                        String.format(
                            // @checkstyle LineLength (1 line)
                            "there is no LF after CR in header, line #%d: \"%s\"",
                            head.size() + 1, new UTF8String(
                                baos.toByteArray()
                            ).string()
                        )
                    );
                }
                if (baos.size() == 0) {
                    break;
                }
                data = new Opt.Single<Integer>(input.read());
                final Opt<String> header = newHeader(data, baos);
                if (header.has()) {
                    head.add(header.get());
                }
                continue;
            }
            baos.write(legalCharacter(data, baos, head.size() + 1));
            data = new Opt.Empty<Integer>();
        }
        if (eof) {
            throw new IOException("empty request");
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
     * Builds current read header.
     * @param data Current read character
     * @param baos Current read header
     * @return Read header
     */
    private static Opt<String> newHeader(final Opt<Integer> data,
        final ByteArrayOutputStream baos) {
        Opt<String> header = new Opt.Empty<String>();
        if (data.get() != ' ' && data.get() != '\t') {
            header = new Opt.Single<String>(
                new UTF8String(baos.toByteArray()).string()
            );
            baos.reset();
        }
        return header;
    }

    /**
     * Returns a legal character based n the read character.
     * @param data Character read
     * @param baos Byte stream containing read header
     * @param position Header line number
     * @return A legal character
     * @throws HttpException if character is illegal
     */
    private static Integer legalCharacter(final Opt<Integer> data,
        final ByteArrayOutputStream baos, final Integer position)
        throws HttpException {
        // @checkstyle MagicNumber (1 line)
        if ((data.get() > 0x7f || data.get() < 0x20)
            && data.get() != '\t') {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format(
                    // @checkstyle LineLength (1 line)
                    "illegal character 0x%02X in HTTP header line #%d: \"%s\"",
                    data.get(),
                    position,
                    new UTF8String(baos.toByteArray()).string()
                )
            );
        }
        return data.get();
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
