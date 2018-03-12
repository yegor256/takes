/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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
package org.takes.facets.cookies;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.misc.EnglishLowerCase;
import org.takes.misc.Sprintf;
import org.takes.misc.VerboseIterable;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqWrap;

/**
 * HTTP cookies parsing.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.14
 */
public interface RqCookies extends Request {
    /**
     * Get single cookie.
     * @param name Cookie name
     * @return List of values (can be empty)
     * @throws IOException If fails
     */
    Iterable<String> cookie(CharSequence name) throws IOException;

    /**
     * Get all cookie names.
     * @return All names
     * @throws IOException If fails
     */
    Iterable<String> names() throws IOException;

    /**
     * Request decorator, for HTTP cookies parsing.
     *
     * <p>The class is immutable and thread-safe.
     * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
     * @version $Id$
     * @since 0.14
     */
    @EqualsAndHashCode(callSuper = true)
    final class Base extends RqWrap implements RqCookies {
        /**
         * Ctor.
         * @param req Original request
         */
        public Base(final Request req) {
            super(req);
        }

        @Override
        public Iterable<String> cookie(final CharSequence key)
            throws IOException {
            final Map<String, String> map = this.map();
            final String value = map.get(
                new EnglishLowerCase(key.toString()).string()
            );
            final Iterable<String> iter;
            if (value == null) {
                iter = new VerboseIterable<String>(
                    Collections.<String>emptyList(),
                    new Sprintf(
                        // @checkstyle LineLengthCheck (1 line)
                        "There are no Cookies by name \"%s\" among %d others: %s",
                        key, map.size(), map.keySet()
                    )
                );
            } else {
                iter = new VerboseIterable<String>(
                    Collections.singleton(value),
                    new Sprintf(
                        "There is always only one Cookie by name \"%s\"",
                        key
                    )
                );
            }
            return iter;
        }

        @Override
        public Iterable<String> names() throws IOException {
            return this.map().keySet();
        }

        /**
         * Parse them all in a map.
         * @return Map of them
         * @throws IOException If fails
         */
        private Map<String, String> map() throws IOException {
            final Map<String, String> map = new HashMap<>(0);
            final Iterable<String> values =
                new RqHeaders.Base(this).header("Cookie");
            for (final String value : values) {
                for (final String pair : value.split(";")) {
                    final String[] parts = pair.split("=", 2);
                    final String key =
                        new EnglishLowerCase(parts[0].trim()).string();
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
}
