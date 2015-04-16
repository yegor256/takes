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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.misc.Sprintf;
import org.takes.misc.VerboseIterable;

/**
 * Request decorator that decodes FORM data from
 * {@code application/x-www-form-urlencoded} format (RFC 1738).
 *
 * <p>For {@code multipart/form-data} format use
 * {@link org.takes.rq.RqMultipart}.
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
 * @see org.takes.rq.RqGreedy
 */
public interface RqForm extends Request {

    /**
     * Get single parameter.
     * @param name Parameter name
     * @return List of values (can be empty)
     */
    Iterable<String> param(CharSequence name);

    /**
     * Get all parameter names.
     * @return All names
     */
    Iterable<String> names();

    /**
     * Base implementation of @link RqForm.
     * @author Aleksey Popov (alopen@yandex.ru)
     * @version $Id$
     */
    @EqualsAndHashCode(callSuper = true, of = "map")
    final class Base extends RqWrap implements RqForm {
        /**
         * Map of params and values.
         */
        private final transient ConcurrentMap<String, List<String>> map;

        /**
         * Ctor.
         * @param req Original request
         * @throws IOException If fails
         */
        @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
        public Base(final Request req) throws IOException {
            super(req);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new RqPrint(req).printBody(baos);
            final String body = new String(baos.toByteArray());
            this.map = new ConcurrentHashMap<String, List<String>>(0);
            for (final String pair : body.split("&")) {
                if (pair.isEmpty()) {
                    continue;
                }
                final String[] parts = pair.split("=", 2);
                if (parts.length < 2) {
                    throw new IOException(
                            String.format("invalid form body pair: %s", pair)
                    );
                }
                final String key = RqForm.Base.decode(
                        parts[0].trim().toLowerCase(Locale.ENGLISH)
                );
                this.map.putIfAbsent(key, new LinkedList<String>());
                this.map.get(key).add(RqForm.Base.decode(parts[1].trim()));
            }
        }

        @Override
        public Iterable<String> param(final CharSequence key) {
            final List<String> values =
                    this.map.get(key.toString().toLowerCase(Locale.ENGLISH));
            final Iterable<String> iter;
            if (values == null) {
                iter = new VerboseIterable<String>(
                    Collections.<String>emptyList(),
                    new Sprintf(
                        "there're no params by name \"%s\" among %d others: %s",
                        key, this.map.size(), this.map.keySet()
                    )
                );
            } else {
                iter = new VerboseIterable<String>(
                    values,
                    new Sprintf(
                        "there are only %d params by name \"%s\"",
                        values.size(), key
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
         * Decode from URL.
         * @param txt Text
         * @return Decoded
         */
        private static String decode(final CharSequence txt) {
            try {
                return URLDecoder.decode(
                        txt.toString(), Charset.defaultCharset().name()
                );
            } catch (final UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }
}
