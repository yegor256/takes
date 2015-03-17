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
import java.util.ArrayList;
import java.util.Collections;
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
public final class RqCookies extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     */
    public RqCookies(final Request req) {
        super(req);
    }

    /**
     * Get single cookie.
     * @param key Cookie name
     * @return List of values (can be empty)
     * @throws IOException If fails
     */
    public Iterable<String> cookie(final String key) throws IOException {
        final String value = this.map().get(
            key.toLowerCase(Locale.ENGLISH)
        );
        final List<String> values = new ArrayList<String>(1);
        if (value != null) {
            values.add(value);
        }
        return Collections.unmodifiableList(values);
    }

    /**
     * Get all cookie names.
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
    private Map<String, String> map() throws IOException {
        final ConcurrentMap<String, String> map =
            new ConcurrentHashMap<String, String>(0);
        final Iterable<String> values = new RqHeaders(this).header("Cookie");
        for (final String value : values) {
            for (final String pair : value.split(";")) {
                final String[] parts = pair.split("=", 2);
                final String key = parts[0].trim().toLowerCase(Locale.ENGLISH);
                if (parts.length > 1 && !parts[1].isEmpty()) {
                    map.put(key, parts[1].trim());
                } else {
                    map.remove(key);
                }
            }
        }
        return map;
    }

}
