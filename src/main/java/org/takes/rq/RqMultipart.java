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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.misc.Sprintf;
import org.takes.misc.VerboseIterable;

/**
 * Request decorator that decodes FORM data from
 * {@code multipart/form-data} format (RFC 2045).
 *
 * <p>For {@code application/x-www-form-urlencoded}
 * format use {@link org.takes.rq.RqForm}.
 *
 * <p>It is highly recommended to use {@link org.takes.rq.RqGreedy}
 * decorator before passing request to this class.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.9
 * @link <a href="http://www.w3.org/TR/html401/interact/forms.html">Forms in HTML</a>
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @see org.takes.rq.RqGreedy
 */
@EqualsAndHashCode(callSuper = true)
public final class RqMultipart extends RqWrap {

    /**
     * Pattern to get boundary from header.
     */
    private static final Pattern BOUNDARY = Pattern.compile(
        ".*[^a-z]boundary=([^;]+).*"
    );

    /**
     * Pattern to get name from header.
     */
    private static final Pattern NAME = Pattern.compile(
        ".*[^a-z]name=\"([^\"]+)\".*"
    );

    /**
     * Map of params and values.
     */
    private final transient ConcurrentMap<String, List<Request>> map;

    /**
     * State enums for parser.
     * @author mda
     *
     */
    private enum State { UNDEF, PBOUNDARY, BOUNDARY, BLOCK };

    /**
     * Ctor.
     * @param req Original request
     * @throws IOException If fails
     */
    public RqMultipart(final Request req) throws IOException {
        super(req);
        final String header = new RqHeaders(req).header("Content-Type")
            .iterator().next();
        if (!header.toLowerCase(Locale.ENGLISH)
            .startsWith("multipart/form-data")) {
            throw new IOException(
                String.format(
                    // @checkstyle LineLength (1 line)
                    "RqMultipart can only parse multipart/form-data, while Content-Type specifies a different type: %s",
                    header
                )
            );
        }
        final Matcher matcher = RqMultipart.BOUNDARY.matcher(header);
        if (!matcher.matches()) {
            throw new IOException(
                String.format(
                    "boundary is not specified in Content-Type header: %s",
                    header
                )
            );
        }
        this.map = RqMultipart.asMap(this.requests(matcher, this.body()));
    }

    /**
     * Get single part.
     * @param name Name of the part to get
     * @return List of parts (can be empty)
     */
    public Iterable<Request> part(final CharSequence name) {
        final List<Request> values = this.map
            .get(name.toString().toLowerCase(Locale.ENGLISH));
        final Iterable<Request> iter;
        if (values == null) {
            iter = new VerboseIterable<Request>(
                Collections.<Request>emptyList(),
                new Sprintf(
                    "there are no parts by name \"%s\" among %d others: %s",
                    name, this.map.size(), this.map.keySet()
                )
            );
        } else {
            iter = new VerboseIterable<Request>(
                values,
                new Sprintf(
                    "there are just %d parts by name \"%s\"",
                    values.size(), name
                )
            );
        }
        return iter;
    }

    /**
     * Get all part names.
     * @return All names
     */
    public Iterable<String> names() {
        return this.map.keySet();
    }

    /**
     * Convert a list of requests to a map.
     * @param reqs Requests
     * @return Map of them
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private static ConcurrentMap<String, List<Request>> asMap(
        final Collection<Request> reqs) throws IOException {
        final ConcurrentMap<String, List<Request>> map =
            new ConcurrentHashMap<String, List<Request>>(reqs.size());
        for (final Request req : reqs) {
            final String header = new RqHeaders(req)
                .header("Content-Disposition").iterator().next();
            final Matcher matcher = RqMultipart.NAME.matcher(header);
            if (!matcher.matches()) {
                throw new IOException(
                    String.format(
                        "\"name\" not found in Content-Disposition header: %s",
                        header
                    )
                );
            }
            final String name = matcher.group(1);
            map.putIfAbsent(name, new LinkedList<Request>());
            map.get(name).add(req);
        }
        return map;
    }

    /**
     * Write header.
     * @param fos Output stream
     * @throws IOException If fails
     */
    private void writeHeader(final OutputStream fos) throws IOException {
        fos.write(this.head().iterator().next().getBytes("UTF-8"));
        fos.write("\r\n".getBytes());
    }

    /**
     * Multi-part request parsing.
     * @param matcher Boundary matcher
     * @param body Request
     * @return Request collection
     * @throws IOException If fails
     * @checkstyle ExecutableStatementCountCheck (40 lines)
     * @todo #115 Need to refactoring this function to avoid
     *  ExecutableStatementCountCheck.
     */
    private Collection<Request> requests(final Matcher matcher,
            final InputStream body) throws IOException {
        final Collection<Request> requests = new LinkedList<Request>();
        final byte[] boundary = new StringBuilder()
            .append("\r\n--")
            .append(matcher.group(1)).toString().getBytes();
        int pos = 2;
        File file = null;
        OutputStream fos = null;
        while (body.available() > 0) {
            int data = body.read();
            if (data < 0) {
                throw new IOException("Unexpected end of request stream.");
            }
            if (pos < boundary.length  &&  data == boundary[pos]) {
                ++pos;
                continue;
            }
            if (pos == boundary.length) {
                pos = 0;
                body.read();
                data = body.read();
                if (fos != null) {
                    fos.flush();
                    fos.close();
                    requests.add(new RqLive(new FileInputStream(file)));
                }
                if (data < 0) {
                    break;
                }
                file = File.createTempFile("takes", "req");
                fos = new BufferedOutputStream(new FileOutputStream(file));
                this.writeHeader(fos);
            } else if (pos > 0) {
                fos.write(boundary, 0, pos);
                pos = 0;
            }
            fos.write(data);
        }
        return requests;
    }

}
