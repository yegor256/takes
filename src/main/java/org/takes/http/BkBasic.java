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
package org.takes.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Takes;
import org.takes.rq.RqPlain;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;
import org.takes.rs.RsWithStatus;

/**
 * Basic back-end.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@EqualsAndHashCode(of = "takes")
public final class BkBasic implements Back {

    /**
     * Takes.
     */
    private final transient Takes takes;

    /**
     * Ctor.
     * @param tks Takes
     */
    public BkBasic(final Takes tks) {
        this.takes = tks;
    }

    @Override
    public void accept(final Socket socket) throws IOException {
        final InputStream input = socket.getInputStream();
        final BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                new BufferedInputStream(input),
                "UTF-8"
            )
        );
        final List<String> head = new LinkedList<String>();
        while (true) {
            final String line = reader.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            head.add(line);
        }
        try {
            this.print(new RqPlain(head, input), socket.getOutputStream());
        } finally {
            input.close();
        }
    }

    /**
     * Print response to output stream, safely.
     * @param req Request
     * @param output Output
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    private void print(final Request req, final OutputStream output)
        throws IOException {
        try {
            new RsPrint(
                this.takes.take(req).print()
            ).print(output);
            // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Throwable ex) {
            new RsPrint(BkBasic.failure(ex)).print(output);
        } finally {
            output.close();
        }
    }

    /**
     * Make a failure response.
     * @param err Error
     * @return Response
     */
    private static Response failure(final Throwable err) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintWriter writer = new PrintWriter(baos);
        err.printStackTrace(writer);
        writer.close();
        return new RsWithStatus(
            new RsText(new ByteArrayInputStream(baos.toByteArray())),
            HttpURLConnection.HTTP_INTERNAL_ERROR
        );
    }

}
