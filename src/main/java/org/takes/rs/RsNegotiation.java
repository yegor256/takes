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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Takes;
import org.takes.rq.RqHeaders;

/**
 * Response that delivers different responses depending on Accept header.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "responses", "request" })
public final class RsNegotiation implements Response {

    /**
     * Map of responses.
     */
    private final transient Map<Collection<String>, Response> responses;

    /**
     * Request.
     */
    private final transient Request request;

    /**
     * Ctor.
     * @param req Request
     */
    public RsNegotiation(final Request req) {
        this(Collections.<Collection<String>, Response>emptyMap(), req);
    }

    /**
     * Ctor.
     * @param map Map of responses
     * @param req Request
     */
    public RsNegotiation(final Map<Collection<String>, Response> map,
        final Request req) {
        this.responses = Collections.unmodifiableMap(map);
        this.request = req;
    }

    /**
     * With this type and response.
     * @param types Types
     * @param res Response
     * @return Response
     */
    public RsNegotiation with(final String types, final Response res) {
        final ConcurrentMap<Collection<String>, Response> map =
            new ConcurrentHashMap<Collection<String>, Response>(
                this.responses.size() + 1
            );
        // @checkstyle MultipleStringLiteralsCheck (1 line)
        final String[] names = types.toLowerCase(Locale.ENGLISH).split(",");
        final Collection<String> list = new ArrayList<String>(names.length);
        for (final String name : names) {
            list.add(name.trim());
        }
        map.putAll(this.responses);
        map.put(list, res);
        return new RsNegotiation(map, this.request);
    }

    @Override
    public List<String> head() throws IOException {
        return this.pick().head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.pick().body();
    }

    /**
     * Pick the right one.
     * @return Response
     * @throws IOException If fails
     */
    private Response pick() throws IOException {
        final Iterable<String> accepted = this.accepted();
        for (final String need : accepted) {
            for (final Map.Entry<Collection<String>, Response> ent
                : this.responses.entrySet()) {
                // @checkstyle NestedForDepthCheck (1 line)
                for (final String has : ent.getKey()) {
                    if (RsNegotiation.match(has, need)) {
                        return ent.getValue();
                    }
                }
            }
        }
        throw new Takes.NotFoundException(
            String.format("page not found for these types: %s", accepted)
        );
    }

    /**
     * Get all types accepted by the client.
     * @return Media types
     * @throws IOException If fails
     */
    private Iterable<String> accepted() throws IOException {
        final SortedMap<Double, String> types = new TreeMap<Double, String>(
            Collections.reverseOrder()
        );
        final List<String> hdrs = new RqHeaders(this.request).header("Accept");
        for (final String hdr : hdrs) {
            for (final String sector : hdr.split(",")) {
                final String[] parts = sector.split(";", 2);
                final String name = parts[0].trim().toLowerCase(Locale.ENGLISH);
                if (parts.length > 1) {
                    types.put(
                        Double.parseDouble(parts[1].trim().substring(2)),
                        name
                    );
                } else {
                    types.put(1.0d, name);
                }
            }
        }
        final Collection<String> accepted = new LinkedList<String>();
        accepted.addAll(types.values());
        if (accepted.isEmpty()) {
            accepted.add("*/*");
        }
        return accepted;
    }

    /**
     * Types match.
     * @param left First type
     * @param right Second
     * @return TRUE if they match
     */
    private static boolean match(final String left, final String right) {
        final String slash = "/";
        final String[] first = left.split(slash);
        final String[] second = right.split(slash);
        return RsNegotiation.same(first[0], second[0])
            && RsNegotiation.same(first[1], second[1]);
    }

    /**
     * Types match.
     * @param left First type
     * @param right Second
     * @return TRUE if they match
     */
    private static boolean same(final String left, final String right) {
        final String star = "*";
        return star.equals(left) || star.equals(right) || left.equals(right);
    }

}
