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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
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
 * HTTP multipart FORM data decoding.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.9
 */
public interface RqMultipart extends Request {
    /**
     * Get single part.
     * @param name Name of the part to get
     * @return List of parts (can be empty)
     */
    Iterable<Request> part(final CharSequence name);

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
        public Base(final Request req) throws IOException {
            super(req);
            final String header = new RqHeaders(req).header("Content-Type")
                .iterator().next();
            if (!header.toLowerCase(Locale.ENGLISH)
                .startsWith("multipart/form-data")) {
                throw new IOException(
                    String.format(
                        // @checkstyle LineLength (1 line)
                        "RqMultipart.Base can only parse multipart/form-data, while Content-Type specifies a different type: %s",
                        header
                    )
                );
            }
            final Matcher matcher = Base.BOUNDARY.matcher(header);
            if (!matcher.matches()) {
                throw new IOException(
                    String.format(
                        "boundary is not specified in Content-Type header: %s",
                        header
                    )
                );
            }
            final Collection<Request> requests = new LinkedList<Request>();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new RqPrint(req).printBody(baos);
            final byte[] boundary = matcher.group(1).getBytes();
            final byte[] body = baos.toByteArray();
            int pos = 0;
            while (pos < body.length) {
                int start = pos + boundary.length + 2;
                if (body[start] == '-') {
                    break;
                }
                start += 2;
                final int stop = RqMultipart.Base.indexOf(
                    body,
                    boundary,
                    start
                ) - 2;
                requests.add(this.make(body, start, stop - 2));
                pos = stop;
            }
            this.map = RqMultipart.Base.asMap(requests);
        }

        /**
         * Get single part.
         * @param name Name of the part to get
         * @return List of parts (can be empty)
         */
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

        /**
         * Get all part names.
         * @return All names
         */
        @Override
        public Iterable<String> names() {
            return this.map.keySet();
        }

        /**
         * Make a request.
         * @param body Body
         * @param start Start position
         * @param stop Stop position
         * @return Request
         * @throws IOException If fails
         */
        private Request make(final byte[] body, final int start,
            final int stop) throws IOException {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(this.head().iterator().next().getBytes());
            baos.write("\r\n".getBytes());
            baos.write(Arrays.copyOfRange(body, start, stop));
            return new RqLive(new ByteArrayInputStream(baos.toByteArray()));
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
                final Matcher matcher = Base.NAME.matcher(header);
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

        /**
         * Find position of array inside another array.
         * @param outer Big array
         * @param inner Small array
         * @param start Where to start searching
         * @return Position
         * @throws IOException If fails
         */
        private static int indexOf(final byte[] outer, final byte[] inner,
            final int start) throws IOException {
            for (int idx = start; idx < outer.length - inner.length; ++idx) {
                boolean found = true;
                for (int sub = 0; sub < inner.length; ++sub) {
                    if (outer[idx + sub] != inner[sub]) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    return idx;
                }
            }
            throw new IOException("closing boundary not found");
        }
    }
}
