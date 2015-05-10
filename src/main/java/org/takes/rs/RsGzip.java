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
package org.takes.rs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import lombok.EqualsAndHashCode;
import org.takes.Response;

/**
 * Response compressed with GZIP, according to RFC 1952.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.10
 */
@EqualsAndHashCode(callSuper = true)
public final class RsGzip extends RsWrap {

    /**
     * Ctor.
     * @param res Original response
     * @throws IOException If fails
     */
    public RsGzip(final Response res) throws IOException {
        super(RsGzip.make(res));
    }

    /**
     * Make a response.
     * @param res Original response
     * @return Response just made
     * @throws IOException If fails
     */
    private static Response make(final Response res) throws IOException {
        return new RsWithHeader(
            new RsWithBody(
                res,
                RsGzip.gzip(res.body())
            ),
            "Content-Encoding",
            "gzip"
        );
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
        // @checkstyle MagicNumberCheck (1 line)
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
