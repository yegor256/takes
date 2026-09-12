/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import lombok.EqualsAndHashCode;
import org.cactoos.text.FormattedText;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.misc.Opt;

/**
 * HTTP request parser that reads from a raw input stream.
 *
 * <p>This class parses HTTP requests directly from input streams,
 * handling the HTTP protocol format including request line parsing,
 * header validation, and proper CRLF line ending handling. It performs
 * strict validation of HTTP format compliance and throws appropriate
 * exceptions for malformed requests.
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

    private static Request parse(final InputStream input) throws IOException {
        boolean eof = true;
        final Collection<String> head = new ArrayList<>(0);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Opt<Integer> data = new Opt.Empty<>();
        data = RqLive.data(input, data, false);
        while (data.get() > 0) {
            eof = false;
            if (data.get() == '\r') {
                RqLive.checkLineFeed(input, baos, head.size() + 1);
                if (baos.size() == 0 && !head.isEmpty()) {
                    break;
                }
                if (baos.size() == 0) {
                    data = RqLive.data(input, new Opt.Empty<>(), false);
                    continue;
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

    private static Integer legalCharacter(final Opt<Integer> data,
        final ByteArrayOutputStream baos, final Integer position)
        throws IOException {
        if ((data.get() > 0x7F || data.get() < 0x20)
            && data.get() != '\t') {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                new UncheckedText(
                    new FormattedText(
                        "illegal character 0x%02X in HTTP header line #%d: \"%s\"",
                        data.get(),
                        position,
                        new TextOf(baos.toByteArray())
                    )
                ).asString()
            );
        }
        return data.get();
    }

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
