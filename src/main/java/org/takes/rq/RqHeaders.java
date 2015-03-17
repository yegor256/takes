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

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request decorator, for HTTP headers parsing.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class RqHeaders extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     */
    public RqHeaders(final Request req) {
        super(req);
    }

    /**
     * Get single header.
     * @param key Header name
     * @return List of values (can be empty)
     * @throws IOException If fails
     */
    public Iterable<String> header(final String key) throws IOException {
        List<String> values = this.map().get(
            key.toLowerCase(Locale.ENGLISH)
        );
        if (values == null) {
            values = Collections.emptyList();
        }
        return Collections.unmodifiableList(values);
    }

    /**
     * Get all header names.
     * @return All names
     * @throws IOException If fails
     */
    public Iterable<String> names() throws IOException {
        return this.map().keySet();
    }

    /**
     * Parse them all in a map.
     * @return Map of them
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private Map<String, List<String>> map() throws IOException {
        final Iterator<String> head = this.head().iterator();
        if (!head.hasNext()) {
            throw new IOException(
                "a valid request must contain at least one line in the head"
            );
        }
        head.next();
        final ConcurrentMap<String, List<String>> map =
            new ConcurrentHashMap<String, List<String>>(0);
        while (head.hasNext()) {
            final String line = head.next();
            final String[] parts = line.split(":", 2);
            if (parts.length < 2) {
                throw new IOException(
                    String.format("invalid HTTP header: \"%s\"", line)
                );
            }
            final String key = parts[0].trim().toLowerCase(Locale.ENGLISH);
            map.putIfAbsent(key, new LinkedList<String>());
            map.get(key).add(parts[1].trim());
        }
        return map;
    }

}
