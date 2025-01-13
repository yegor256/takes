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
package org.takes.rq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.LinkedList;
import lombok.EqualsAndHashCode;
import org.cactoos.text.FormattedText;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.misc.Opt;

/**
 * Live request.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
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
     * @checkstyle ExecutableStatementCountCheck (100 lines)
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private static Request parse(final InputStream input) throws IOException {
        boolean eof = true;
        final Collection<String> head = new LinkedList<>();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Opt<Integer> data = new Opt.Empty<>();
        data = RqLive.data(input, data, false);
        while (data.get() > 0) {
            eof = false;
            if (data.get() == '\r') {
                RqLive.checkLineFeed(input, baos, head.size() + 1);
                if (baos.size() == 0) {
                    break;
                }
                data = new Opt.Single<>(input.read());
                final Opt<String> header = RqLive.newHeader(data, baos);
                if (header.has()) {
                    head.add(header.get());
                }
                data = RqLive.data(input, data, false);
                continue;
            }
            baos.write(RqLive.legalCharacter(data, baos, head.size() + 1));
            data = RqLive.data(input, new Opt.Empty<>(), true);
        }
        if (eof) {
            throw new IOException("empty request");
        }
        return new RequestOf(head, input);
    }

    /**
     * Checks whether or not the next byte to read is a Line Feed.
     * <p><i>Please note that this method assumes that the previous byte read
     * was a Carriage Return.</i>
     * @param input The input stream to read
     * @param baos Current read header
     * @param position Header line number
     * @throws IOException If the next byte is not a Line Feed as expected
     */
    private static void checkLineFeed(final InputStream input,
        final ByteArrayOutputStream baos, final Integer position)
        throws IOException {
        if (input.read() != '\n') {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                new FormattedText(
                    "there is no LF after CR in header, line #%d: \"%s\"",
                    position,
                    new TextOf(baos.toByteArray())
                ).toString()
            );
        }
    }

    /**
     * Builds current read header.
     * @param data Current read character
     * @param baos Current read header
     * @return Read header
     */
    private static Opt<String> newHeader(final Opt<Integer> data,
        final ByteArrayOutputStream baos) {
        Opt<String> header = new Opt.Empty<>();
        if (data.get() != ' ' && data.get() != '\t') {
            header = new Opt.Single<>(
                new UncheckedText(
                    new TextOf(
                        baos.toByteArray()
                    )
                ).asString()
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
     * @throws IOException if character is illegal
     */
    private static Integer legalCharacter(final Opt<Integer> data,
        final ByteArrayOutputStream baos, final Integer position)
        throws IOException {
        if ((data.get() > 0x7f || data.get() < 0x20)
            && data.get() != '\t') {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format(
                    "illegal character 0x%02X in HTTP header line #%d: \"%s\"",
                    data.get(),
                    position,
                    new TextOf(baos.toByteArray())
                )
            );
        }
        return data.get();
    }

    /**
     * Obtains new byte if hasn't.
     * @param input Stream
     * @param data Empty or current data
     * @param available Indicates whether or not it should check first if there
     *  are available bytes
     * @return Next or current data
     * @throws IOException if input.read() fails
     */
    private static Opt<Integer> data(final InputStream input,
        final Opt<Integer> data, final boolean available) throws IOException {
        final Opt<Integer> ret;
        if (data.has()) {
            ret = data;
        } else if (available && input.available() <= 0) {
            ret = new Opt.Single<>(-1);
        } else {
            ret = new Opt.Single<>(input.read());
        }
        return ret;
    }
}
