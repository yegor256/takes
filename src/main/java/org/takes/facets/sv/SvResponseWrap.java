/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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
package org.takes.facets.sv;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletResponse;
import org.takes.Response;

/**
 * Writes Response to ServletResponse.
 *
 * @author Izbassar Tolegen (t.izbassar@gmail.com)
 * @version $Id.$
 * @since 2.0
 */
final class SvResponseWrap {

    /**
     * Length of the buffer to use when reading from body.
     */
    private static final int BUFFER_LENGTH = 1024;

    /**
     * Origin response.
     */
    private final Response origin;

    /**
     * Ctor.
     * @param res Response.
     */
    SvResponseWrap(final Response res) {
        this.origin = res;
    }

    /**
     * Prints Response to ServletResponse.
     * @param response ServletResponse to print to.
     * @throws IOException If fails.
     */
    public void print(final HttpServletResponse response) throws IOException {
        final Iterable<String> head = this.origin.head();
        for (final String header : head) {
            final String[] split = header.split(":");
            final String name = split[0];
            final String value = split[1];
            response.addHeader(name, value);
        }
        final InputStream body = this.origin.body();
        response.getWriter().write(SvResponseWrap.read(body));
    }

    /**
     * Reads inputStream to String.
     * @param body Body to read.
     * @return Content of body.
     * @throws IOException If fails.
     */
    @SuppressWarnings("PMD.AssignmentInOperand")
    private static String read(final InputStream body) throws IOException {
        try (ByteArrayOutputStream data = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[SvResponseWrap.BUFFER_LENGTH];
            // @checkstyle InnerAssignmentCheck (1 line)
            for (int len; (len = body.read(buffer)) != -1;) {
                data.write(buffer, 0, len);
            }
            return data.toString(Charset.defaultCharset().name());
        }
    }
}
