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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
     * @see <a href="http://www.w3.org/TR/html401/interact/forms.html">
     *  Forms in HTML</a>
     * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
     * @see RqGreedy
     */
    @EqualsAndHashCode(callSuper = true)
    final class Base extends RqWrap implements RqMultipart {
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
        private final transient Map<String, List<Request>> map;
        /**
         * Internal buffer.
         */
        private final transient ByteBuffer buffer;
        /**
         * InputStream based on request body.
         */
        private final transient InputStream stream;
        /**
         * Ctor.
         * @param req Original request
         * @throws IOException If fails
         * @checkstyle ExecutableStatementCountCheck (2 lines)
         */
        public Base(final Request req) throws IOException {
            super(req);
            this.stream = new RqLengthAware(req).body();
            this.buffer = ByteBuffer.allocate(
                // @checkstyle MagicNumberCheck (1 line)
                Math.min(8192, this.stream.available())
            );
            this.map = this.requests(req);
        }
        @Override
        public Iterable<Request> part(final CharSequence name) {
            final List<Request> values = this.map
                .get(name.toString().toLowerCase(Locale.ENGLISH));
            final Iterable<Request> iter;
            if (values == null) {
                iter = new VerboseIterable<>(
                    Collections.<Request>emptyList(),
                    new Sprintf(
                        "there are no parts by name \"%s\" among %d others: %s",
                        name, this.map.size(), this.map.keySet()
                    )
                );
            } else {
                iter = new VerboseIterable<>(
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
         * Build a request for each part of the origin request.
         * @param req Origin request
         * @return The requests map that use the part name as a map key
         * @throws IOException If fails
         */
        private Map<String, List<Request>> requests(
            final Request req) throws IOException {
            final String header = new RqHeaders.Smart(
                new RqHeaders.Base(req)
            // @checkstyle MultipleStringLiteralsCheck (1 line)
            ).single("Content-Type");
            if (!header.toLowerCase(Locale.ENGLISH)
                .startsWith("multipart/form-data")) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    String.format(
                        // @checkstyle LineLength (1 line)
                        "RqMultipart.Base can only parse multipart/form-data, while Content-Type specifies a different type: \"%s\"",
                        header
                    )
                );
            }
            final Matcher matcher = RqMultipart.Base.BOUNDARY.matcher(header);
            if (!matcher.matches()) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    String.format(
                        // @checkstyle LineLength (1 line)
                        "boundary is not specified in Content-Type header: \"%s\"",
                        header
                    )
                );
            }
            final ReadableByteChannel body = Channels.newChannel(this.stream);
            if (body.read(this.buffer) < 0) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    "failed to read the request body"
                );
            }
            final byte[] boundary = String.format(
                "\r\n--%s", matcher.group(1)
            ).getBytes(StandardCharsets.UTF_8);
            this.buffer.flip();
            this.buffer.position(boundary.length - 2);
            final Collection<Request> requests = new LinkedList<>();
            while (this.buffer.hasRemaining()) {
                final byte data = this.buffer.get();
                if (data == '-') {
                    break;
                }
                this.buffer.position(this.buffer.position() + 1);
                requests.add(this.make(boundary, body));
            }
            return RqMultipart.Base.asMap(requests);
        }
        /**
         * Make a request.
         *  Scans the origin request until the boundary reached. Caches
         *  the  content into a temporary file and returns it as a new request.
         * @param boundary Boundary
         * @param body Origin request body
         * @return Request
         * @throws IOException If fails
         * @todo #254:30min in order to delete temporary files InputStream
         *  instance on Request.body should be closed. In context of multipart
         *  requests that means that body of all parts should be closed once
         *  they are not needed anymore.
         */
        private Request make(final byte[] boundary,
            final ReadableByteChannel body) throws IOException {
            final File file = File.createTempFile(
                RqMultipart.class.getName(), ".tmp"
            );
            final FileChannel channel = new RandomAccessFile(
                file, "rw"
            ).getChannel();
            try {
                channel.write(
                    ByteBuffer.wrap(
                        this.head().iterator().next().getBytes(
                            StandardCharsets.UTF_8
                        )
                    )
                );
                // @checkstyle MultipleStringLiteralsCheck (1 line)
                channel.write(
                    ByteBuffer.wrap(
                        RqMultipart.Fake.CRLF.getBytes(StandardCharsets.UTF_8)
                    )
                );
                this.copy(channel, boundary, body);
            } finally {
                channel.close();
            }
            return new RqWithHeader(
                new RqLive(
                    new TempInputStream(
                        new FileInputStream(file),
                        file
                    )
                ),
                "Content-Length",
                String.valueOf(file.length())
            );
        }
        /**
         * Copy until boundary reached.
         * @param target Output file channel
         * @param boundary Boundary
         * @param body Origin request body
         * @throws IOException If fails
         * @checkstyle ExecutableStatementCountCheck (2 lines)
         */
        private void copy(final WritableByteChannel target,
            final byte[] boundary, final ReadableByteChannel body)
            throws IOException {
            int match = 0;
            boolean cont = true;
            while (cont) {
                if (!this.buffer.hasRemaining()) {
                    this.buffer.clear();
                    for (int idx = 0; idx < match; ++idx) {
                        this.buffer.put(boundary[idx]);
                    }
                    match = 0;
                    if (body.read(this.buffer) == -1) {
                        break;
                    }
                    this.buffer.flip();
                }
                final ByteBuffer btarget = this.buffer.slice();
                final int offset = this.buffer.position();
                btarget.limit(0);
                while (this.buffer.hasRemaining()) {
                    final byte data = this.buffer.get();
                    if (data == boundary[match]) {
                        ++match;
                        if (match == boundary.length) {
                            cont = false;
                            break;
                        }
                    } else {
                        match = 0;
                        btarget.limit(this.buffer.position() - offset);
                    }
                }
                target.write(btarget);
            }
        }
        /**
         * Convert a list of requests to a map.
         * @param reqs Requests
         * @return Map of them
         * @throws IOException If fails
         */
        @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
        private static Map<String, List<Request>> asMap(
            final Collection<Request> reqs) throws IOException {
            final Map<String, List<Request>> map = new HashMap<>(reqs.size());
            for (final Request req : reqs) {
                final String header = new RqHeaders.Smart(
                    new RqHeaders.Base(req)
                // @checkstyle MultipleStringLiteralsCheck (1 line)
                ).single("Content-Disposition");
                final Matcher matcher = RqMultipart.Base.NAME.matcher(header);
                if (!matcher.matches()) {
                    throw new HttpException(
                        HttpURLConnection.HTTP_BAD_REQUEST,
                        String.format(
                            // @checkstyle LineLength (1 line)
                            "\"name\" not found in Content-Disposition header: %s",
                            header
                        )
                    );
                }
                final String name = matcher.group(1);
                if (!map.containsKey(name)) {
                    map.put(name, new LinkedList<Request>());
                }
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
            final Iterator<Request> parts = this.part(name).iterator();
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

    /**
     * Fake decorator.
     * @since 0.16
     */
    final class Fake implements RqMultipart {
        /**
         * Fake boundary constant.
         */
        private static final String BOUNDARY = "AaB02x";
        /**
         * Carriage return constant.
         */
        private static final String CRLF = "\r\n";
        /**
         * Fake multipart request.
         */
        private final RqMultipart fake;
        /**
         * Fake ctor.
         * @param req Fake request header holder
         * @param dispositions Fake request body parts
         * @throws IOException If fails
         */
        public Fake(final Request req, final Request... dispositions)
            throws IOException {
            this.fake = new RqMultipart.Base(
                new RqMultipart.Fake.FakeMultipartRequest(
                    req, dispositions
                )
            );
        }
        @Override
        public Iterable<Request> part(final CharSequence name) {
            return this.fake.part(name);
        }
        @Override
        public Iterable<String> names() {
            return this.fake.names();
        }
        @Override
        public Iterable<String> head() throws IOException {
            return this.fake.head();
        }
        @Override
        public InputStream body() throws IOException {
            return this.fake.body();
        }
        /**
         * Fake body stream creator.
         * @param parts Fake request body parts
         * @return InputStream of given dispositions
         * @throws IOException If fails
         */
        private static InputStream fakeStream(final Request... parts)
            throws IOException {
            return new ByteArrayInputStream(
                RqMultipart.Fake.fakeBody(parts).toString().getBytes(
                    StandardCharsets.UTF_8
                )
            );
        }

        /**
         * Fake body creator.
         * @param parts Fake request body parts
         * @return StringBuilder of given dispositions
         * @throws IOException If fails
         */
        @SuppressWarnings("PMD.InsufficientStringBufferDeclaration")
        private static StringBuilder fakeBody(final Request... parts)
            throws IOException {
            final StringBuilder builder = new StringBuilder();
            for (final Request part : parts) {
                builder.append(String.format("--%s", RqMultipart.Fake.BOUNDARY))
                    .append(RqMultipart.Fake.CRLF)
                    // @checkstyle MultipleStringLiteralsCheck (1 line)
                    .append("Content-Disposition: ")
                    .append(
                        new RqHeaders.Smart(
                            new RqHeaders.Base(part)
                        // @checkstyle MultipleStringLiteralsCheck (1 line)
                        ).single("Content-Disposition")
                    ).append(RqMultipart.Fake.CRLF);
                final String body = new RqPrint(part).printBody();
                if (!(RqMultipart.Fake.CRLF.equals(body) || body.isEmpty())) {
                    builder.append(RqMultipart.Fake.CRLF)
                        .append(body)
                        .append(RqMultipart.Fake.CRLF);
                }
            }
            builder.append("Content-Transfer-Encoding: utf-8")
                .append(RqMultipart.Fake.CRLF)
                .append(String.format("--%s--", RqMultipart.Fake.BOUNDARY));
            return builder;
        }

        /**
         * This class is using a decorator pattern for representing
         * a fake HTTP multipart request.
         */
        private static final class FakeMultipartRequest implements Request {
            /**
             * Request object. Holds a value for the header.
             */
            private final Request req;
            /**
             * Holding multiple request body parts.
             */
            private final Request[] parts;
            /**
             * The Constructor for the class.
             * @param rqst The Request object
             * @param list The sequence of dispositions
             */
            FakeMultipartRequest(final Request rqst, final Request... list) {
                this.req = rqst;
                this.parts = list;
            }
            @Override
            public Iterable<String> head() throws IOException {
                return new RqWithHeaders(
                    this.req,
                    String.format(
                        "Content-Type: multipart/form-data; boundary=%s",
                        RqMultipart.Fake.BOUNDARY
                    ),
                    String.format(
                        "Content-Length: %s",
                        RqMultipart.Fake.fakeBody(this.parts).length()
                    )
                ).head();
            }
            @Override
            public InputStream body() throws IOException {
                return RqMultipart.Fake.fakeStream(this.parts);
            }
        }
    }

}
