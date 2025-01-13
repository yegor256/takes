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
 * Response compressed with GZIP, according to RFC 1952.
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
        final OutputStream gzip = new GZIPOutputStream(baos);
        try {
            while (true) {
                final int len = input.read(buf);
                if (len < 0) {
                    break;
                }
                gzip.write(buf, 0, len);
            }
        } finally {
            gzip.close();
            input.close();
        }
        return baos.toByteArray();
    }

}
