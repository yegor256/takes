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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.misc.Sprintf;
import org.takes.misc.VerboseIterable;

/**
 * HTTP multipart FORM data decoding.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.9
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface RqMultipart extends Request {

    /**
     * Get single part.
     * @param name Name of the part to get
     * @return List of parts (can be empty)
     */
    Iterable<Request> part(CharSequence name);

    /**
     * Get all part names.
     * @return All names
     */
    Iterable<String> names();

    /**
     * Request decorator, that decodes FORM data from
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
    final class Base extends RqWrap implements RqMultipart {
        /**
         * Pattern to get boundary from header.
         */
        @SuppressWarnings("PMD.UnusedPrivateField")
        private static final Pattern BOUNDARY = Pattern.compile(
            ".*[^a-z]boundary=([^;]+).*"
        );
        /**
         * Pattern to get name from header.
         */
        @SuppressWarnings("PMD.UnusedPrivateField")
        private static final Pattern NAME = Pattern.compile(
            ".*[^a-z]name=\"([^\"]+)\".*"
        );
        /**
         * Map of params and values.
         */
        private final transient ConcurrentMap<String, List<Request>> map;
        /**
         * Ctor.
         * @param req Original request
         * @throws IOException If fails
         */
        public Base(final Request req) throws IOException {
            super(req);
            final String header = new RqHeaders(req).header("Content-Type")
                .iterator().next();
            if (!header.toLowerCase(Locale.ENGLISH)
                .startsWith("multipart/form-data")) {
                throw new IOException(
                    String.format(
                        // @checkstyle LineLength (1 line)
                        "RqMultipart.Base can only parse multipart/form-data, while Content-Type specifies a different type: \"%s\"",
                        header
                    )
                );
            }
            final Matcher matcher = RqMultipart.Base.BOUNDARY.matcher(header);
            if (!matcher.matches()) {
                throw new IOException(
                    String.format(
                        // @checkstyle LineLength (1 line)
                        "boundary is not specified in Content-Type header: \"%s\"",
                        header
                    )
                );
            }
            final Collection<Request> requests = new LinkedList<Request>();
            final byte[] boundary = String.format(
                "\r\n--%s", matcher.group(1)
            ).getBytes();
            final InputStream body = req.body();
            RqMultipart.Base.skip(body, boundary.length - 2);
            while (body.available() > 0) {
                if (body.read() == '-') {
                    break;
                }
                RqMultipart.Base.skip(body, 1);
                requests.add(this.make(body, boundary));
            }
            this.map = RqMultipart.Base.asMap(requests);
        }
        @Override
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
        @Override
        public Iterable<String> names() {
            return this.map.keySet();
        }
        /**
         * Skip a few bytes in a stream.
         * @param stream The stream
         * @param skip How many bytes to skip
         * @throws IOException If fails
         */
        private static void skip(final InputStream stream, final int skip)
            throws IOException {
            for (int idx = 0; idx < skip; ++idx) {
                stream.read();
            }
        }
        /**
         * Make a request.
         * @param body Body
         * @param boundary Boundary
         * @return Request
         * @throws IOException If fails
         */
        private Request make(final InputStream body,
            final byte[] boundary) throws IOException {
            final File file = File.createTempFile(
                RqMultipart.class.getName(), ".tmp"
            );
            file.deleteOnExit();
            final OutputStream out = new BufferedOutputStream(
                new FileOutputStream(file)
            );
            try {
                out.write(this.head().iterator().next().getBytes());
                out.write("\r\n".getBytes());
                RqMultipart.Base.copy(body, out, boundary);
            } finally {
                out.close();
            }
            return new RqLive(new FileInputStream(file));
        }
        /**
         * Copy until boundary reached.
         * @param body Input stream
         * @param output Output to write to
         * @param boundary Boundary
         * @throws IOException If fails
         */
        private static void copy(final InputStream body,
            final OutputStream output, final byte[] boundary)
            throws IOException {
            int match = 0;
            final ByteArrayOutputStream buf = new ByteArrayOutputStream();
            while (body.available() > 0) {
                final int data = body.read();
                if (data < 0) {
                    break;
                }
                if (data == boundary[match]) {
                    ++match;
                    buf.write(data);
                    if (match == boundary.length) {
                        break;
                    }
                } else {
                    match = 0;
                    output.write(buf.toByteArray());
                    buf.reset();
                    output.write(data);
                }
            }
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
                final Matcher matcher = RqMultipart.Base.NAME.matcher(header);
                if (!matcher.matches()) {
                    throw new IOException(
                        String.format(
                            // @checkstyle LineLength (1 line)
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
    }

    /**
     * Smart decorator.
     * @since 0.15
     */
    final class Smart implements RqMultipart {
        /**
         * Original request.
         */
        private final transient RqMultipart origin;
        /**
         * Ctor.
         * @param req Original
         */
        public Smart(final RqMultipart req) {
            this.origin = req;
        }
        /**
         * Get single part.
         * @param name Name of the part to get
         * @return Part
         * @throws HttpException If fails
         */
        public Request single(final CharSequence name) throws HttpException {
            final Iterator<Request> parts = this.origin
                .part(name).iterator();
            if (!parts.hasNext()) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    String.format(
                        "form param \"%s\" is mandatory", name
                    )
                );
            }
            return parts.next();
        }
        @Override
        public Iterable<Request> part(final CharSequence name) {
            return this.origin.part(name);
        }
        @Override
        public Iterable<String> names() {
            return this.origin.names();
        }
        @Override
        public Iterable<String> head() throws IOException {
            return this.origin.head();
        }
        @Override
        public InputStream body() throws IOException {
            return this.origin.body();
        }
    }

}
