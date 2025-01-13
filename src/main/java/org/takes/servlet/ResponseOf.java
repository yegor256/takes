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
package org.takes.servlet;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cactoos.Text;
import org.cactoos.text.Lowered;
import org.cactoos.text.Split;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;
import org.cactoos.text.UncheckedText;
import org.takes.Response;
import org.takes.misc.Equality;

/**
 * Takes response as servlet response.
 *
 * @since 2.0
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
                InputStream body = this.rsp.body();
                OutputStream out = sresp.getOutputStream()
            ) {
                final byte[] buff = new byte[ResponseOf.BUFSIZE];
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
        final Iterator<Text> split = new Split(header, ":").iterator();
        final UncheckedText name = new UncheckedText(new Trimmed(split.next()));
        final UncheckedText val = new UncheckedText(new Trimmed(split.next()));
        if (new Equality<Text>(
            new TextOf("set-cookie"),
            new Lowered(name)
        ).value()
        ) {
            for (final HttpCookie cck : HttpCookie.parse(header)) {
                sresp.addCookie(
                    new Cookie(cck.getName(), cck.getValue())
                );
            }
        } else {
            sresp.setHeader(name.asString(), val.asString());
        }
    }
}
