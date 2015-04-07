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
        final Collection<Request> requests = new LinkedList<Request>();
        final Parser parser = new Parser(
            this.body(),
            matcher.group(1),
            this.head().iterator().next().getBytes("UTF-8")
        );
        while (parser.hasNext()) {
            requests.add(parser.next());
        }
        this.map = RqMultipart.asMap(requests);
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
     * Multipart parser class that parses input request into temporary files.
     * One part per one file.
     * @author Dmitry Molotchko (dima.molotchko@gmail.com)
     *
     */
    private static class Parser {
        /**
         * Exception message.
         */
        private static final String EXCEPTION =
            "Unexpected end of stream in part reading (no last boundary).";
        /**
         * States enum.
         */
        private enum State {
            /**
             * Begin state.
             */
            BEGIN,
            /**
             * Block state.
             */
            BLOCK,
            /**
             * End state.
             */
            END,
        }
        /**
         * State.
         */
        private transient State state;
        /**
         * Input stream for parsing.
         */
        private final transient InputStream input;
        /**
         * Part boundary.
         */
        private final transient byte[] boundary;
        /**
         * Original request header.
         */
        private final transient byte[] header;
        /**
         * Ctor.
         * @param intp Input stream
         * @param bndary Part boundary
         * @param hdr Original request header
         */
        public Parser(final InputStream intp, final String bndary,
                final byte[] hdr) {
            this.input = intp;
            this.state = State.BEGIN;
            this.boundary = new StringBuilder()
                .append("\r\n--")
                .append(bndary).toString().getBytes();
            this.header = new byte[hdr.length];
            for (int ind = 0;  ind < hdr.length;  ++ind) {
                this.header[ind] = hdr[ind];
            }
        }
        /**
         * Has next part?
         * @return True or false
         */
        public boolean hasNext() {
            return this.state != State.END;
        }
        /**
         * Parses stream into request.
         * @return Parsed part in Request form or NULL if end of parts.
         * @throws IOException If fails
         * @checkstyle NPathComplexityCheck (500 lines)
         * @checkstyle CyclomaticComplexityCheck (500 lines)
         * @checkstyle ExecutableStatementCount (500 lines)
         * @checkstyle NestedIfDepthCheck (500 lines)
         * @todo #115 Refactor this function to avoid checkstyle errors
         */
        public Request next() throws IOException {
            if (this.state == State.BEGIN) {
                for (int ind = 2;  ind < this.boundary.length;  ++ind) {
                    final int data = this.input.read();
                    if (data < 0) {
                        // @checkstyle LineLengthCheck (1 line)
                        throw new IOException("Unexpected end of stream in first boundary reading.");
                    }
                    if (this.boundary[ind] != (byte) data) {
                        // @checkstyle LineLengthCheck (1 line)
                        throw new IOException("Stream is not started with boundary.");
                    }
                }
                this.read('\r');
                this.read('\n');
                this.state = State.BLOCK;
            }
            if (this.state == State.BLOCK) {
                final File file = File.createTempFile("takes", "req");
                final OutputStream fos = new BufferedOutputStream(
                    new FileOutputStream(file)
                );
                fos.write(this.header);
                fos.write("\r\n".getBytes());
                try {
                    int pos = 0;
                    while (true) {
                        final int data = this.input.read();
                        if (data < 0) {
                            // @checkstyle LineLengthCheck (1 line)
                            throw new IOException(EXCEPTION);
                        }
                        if (pos < this.boundary.length
                            && data == this.boundary[pos]) {
                            ++pos;
                            if (pos == this.boundary.length) {
                                final int first = this.input.read();
                                final int second = this.input.read();
                                if (first < 0 || second < 0) {
                                    // @checkstyle LineLengthCheck (1 line)
                                    throw new IOException(EXCEPTION);
                                }
                                if ((byte) first == '\r'
                                    && (byte) second == '\n') {
                                    break;
                                } else if ((byte) first == '-'
                                    && (byte) second == '-') {
                                    this.state = State.END;
                                    break;
                                }
                                // @checkstyle LineLengthCheck (1 line)
                                throw new IOException("Not valid end boundary in stream.");
                            }
                            continue;
                        }
                        if (pos > 0) {
                            fos.write(this.boundary, 0, pos);
                            pos = 0;
                        }
                        fos.write(data);
                    }
                } finally {
                    fos.flush();
                    fos.close();
                }
                return new RqLive(new FileInputStream(file));
            }
            return null;
        }
        /**
         * Read character from input stream.
         * @param character Expected character
         * @return Readed character
         * @throws IOException If end of stream or expected character
         *  is not equals readed character
         */
        private int read(final char character) throws IOException {
            final int chr = this.input.read();
            if (chr < 0 || (byte) chr != character) {
                throw new IOException("Unexpected end of stream.");
            }
            return chr;
        }
    }
}
