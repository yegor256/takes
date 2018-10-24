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
package org.takes.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.takes.Response;

/**
 * Takes response as servlet response.
 *
 * @author Kirill (g4s8.public@gmail.com)
 * @version $Id $
 * @since 2.0
 * @todo #682:30min Servlet request and response adapters are not unit-tested.
 *  There should be tests for reading headers and body from servlet request
 *  and test for validating servlet response after applying takes request.
 */
final class ResponseOf {
    /**
     * Buffer size.
     */
    private static final int BUFSIZE = 8192;
    /**
     * Http response first line head pattern.
     */
    private static final Pattern HTTP_MATCHER = Pattern.compile(
        "^HTTP/(?:1\\.1|2) (?<code>\\d+).*$",
        Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE
    );
    /**
     * Origin response.
     */
    private final Response rsp;
    /**
     * Ctor.
     * @param response Origin takes response
     */
    ResponseOf(final Response response) {
        this.rsp = response;
    }
    /**
     * Apply to servlet response.
     * @param sresp Servlet response
     * @throws IOException If fails
     */
    public void applyTo(final HttpServletResponse sresp) throws IOException {
        final Iterator<String> head = this.rsp.head().iterator();
        final Matcher matcher = ResponseOf.HTTP_MATCHER.matcher(head.next());
        if (matcher.matches()) {
            sresp.setStatus(Integer.parseInt(matcher.group(1)));
            while (head.hasNext()) {
                ResponseOf.applyHeader(sresp, head.next());
            }
            try (
                final InputStream body = this.rsp.body();
                final OutputStream out = sresp.getOutputStream()
            ) {
                final byte[] buff = new byte[ResponseOf.BUFSIZE];
                // @checkstyle LineLengthCheck (1 line)
                for (int read = body.read(buff); read >= 0; read = body.read(buff)) {
                    out.write(buff);
                }
            }
        } else {
            throw new IOException("Invalid response: response code not found");
        }
    }

    /**
     * Apply header to servlet response.
     * @param sresp Response
     * @param header Header
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private static void applyHeader(final HttpServletResponse sresp,
        final String header) {
        final String[] parts = header.split(":");
        final String name = parts[0].trim();
        final String val = parts[1].trim();
        if ("set-cookie".equals(name.toLowerCase(Locale.getDefault()))) {
            for (final HttpCookie cck : HttpCookie.parse(header)) {
                sresp.addCookie(
                    new Cookie(cck.getName(), cck.getValue())
                );
            }
        } else {
            sresp.setHeader(name, val);
        }
    }
}
