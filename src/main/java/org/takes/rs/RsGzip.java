/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.GZIPOutputStream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;

/**
 * Response decorator that compresses the response body using GZIP compression.
 *
 * <p>This decorator compresses the response body using GZIP compression
 * according to RFC 1952 and adds the appropriate Content-Encoding header.
 * The compression is performed lazily when the response is first accessed
 * and cached for subsequent requests. This can significantly reduce
 * bandwidth usage for text-based responses.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.10
 */
@ToString(of = "origin")
@EqualsAndHashCode
public final class RsGzip implements Response {

    /**
     * Original response.
     */
    private final Response origin;

    /**
     * Compressed and cached response.
     */
    private final List<Response> zipped;

    /**
     * Ctor.
     * @param res Original response
     */
    public RsGzip(final Response res) {
        this.zipped = new CopyOnWriteArrayList<>();
        this.origin = res;
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.make().head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.make().body();
    }

    /**
     * Make a response.
     * @return Response just made
     * @throws IOException If fails
     */
    private Response make() throws IOException {
        if (this.zipped.isEmpty()) {
            this.zipped.add(
                new RsWithHeader(
                    new RsWithBody(
                        this.origin,
                        RsGzip.gzip(this.origin.body())
                    ),
                    "Content-Encoding",
                    "gzip"
                )
            );
        }
        return this.zipped.get(0);
    }

    /**
     * Gzip input stream.
     * @param input Input stream
     * @return New input stream
     * @throws IOException If fails
     */
    private static byte[] gzip(final InputStream input)
        throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] buf = new byte[4096];
        try (
            InputStream stream = input;
            OutputStream gzip = new GZIPOutputStream(baos)
        ) {
            while (true) {
                final int len = stream.read(buf);
                if (len < 0) {
                    break;
                }
                gzip.write(buf, 0, len);
            }
        }
        return baos.toByteArray();
    }

}
