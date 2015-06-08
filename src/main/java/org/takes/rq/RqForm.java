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

import com.jcabi.aspects.Cacheable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.EqualsAndHashCode;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.misc.Sprintf;
import org.takes.misc.VerboseIterable;

/**
 * Request decorator that decodes FORM data from
 * {@code application/x-www-form-urlencoded} format (RFC 1738).
 *
 * <p>For {@code multipart/form-data} format use
 * {@link org.takes.rq.RqMultipart.Base}.
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
@SuppressWarnings("PMD.TooManyMethods")
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
    @EqualsAndHashCode(callSuper = true, of = "req")
    final class Base extends RqWrap implements RqForm {

        /**
         * Request.
         */
        private final transient Request req;

        /**
         * Ctor.
         * @param request Original request
         * @throws IOException If fails
         */
        @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
        public Base(final Request request) throws IOException {
            super(request);
            this.req = request;
        }
        @Override
        public Iterable<String> param(final CharSequence key) {
            final List<String> values =
                this.map().get(key.toString().toLowerCase(Locale.ENGLISH));
            final Iterable<String> iter;
            if (values == null) {
                iter = new VerboseIterable<String>(
                    Collections.<String>emptyList(),
                    new Sprintf(
                        "there are no params \"%s\" among %d others: %s",
                        key, this.map().size(), this.map().keySet()
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
            return this.map().keySet();
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
        /**
         * Create map of request parameter.
         * @return Parameters map or empty map in case of error.
         */
        @Cacheable(forever = true)
        private ConcurrentMap<String, List<String>> map()  {
            try {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                new RqPrint(this.req).printBody(baos);
                final String body = new String(baos.toByteArray());
                final ConcurrentMap<String, List<String>> map =
                    new ConcurrentHashMap<String, List<String>>(0);
                for (final String pair : body.split("&")) {
                    if (pair.isEmpty()) {
                        continue;
                    }
                    final String[] parts = pair.split("=", 2);
                    if (parts.length < 2) {
                        throw new HttpException(
                            HttpURLConnection.HTTP_BAD_REQUEST,
                            String.format("invalid form body pair: %s", pair)
                        );
                    }
                    final String key = RqForm.Base.decode(
                        parts[0].trim().toLowerCase(Locale.ENGLISH)
                    );
                    map.putIfAbsent(key, new LinkedList<String>());
                    map.get(key).add(RqForm.Base.decode(parts[1].trim()));
                }
                return map;
            } catch (final IOException ex) {
                return new ConcurrentHashMap<String, List<String>>(0);
            }
        }
    }
    /**
     * Smart decorator, with extra features.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @author Yegor Bugayenko (yegor@teamed.io)
     * @since 0.14
     */
    @EqualsAndHashCode(of = "origin")
    final class Smart implements RqForm {
        /**
         * Original.
         */
        private final transient RqForm origin;
        /**
         * Ctor.
         * @param req Original request
         */
        public Smart(final RqForm req) {
            this.origin = req;
        }
        @Override
        public Iterable<String> param(final CharSequence name) {
            return this.origin.param(name);
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
        /**
         * Get single param or throw HTTP exception.
         * @param name Name of query param
         * @return Value of it
         * @throws IOException If fails
         */
        public String single(final CharSequence name) throws IOException {
            final Iterator<String> params = this.param(name).iterator();
            if (!params.hasNext()) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    String.format(
                        "form param \"%s\" is mandatory", name
                    )
                );
            }
            return params.next();
        }
        /**
         * Get single param or default.
         * @param name Name of query param
         * @param def Default, if not found
         * @return Value of it
         */
        public String single(final CharSequence name, final String def) {
            final String value;
            final Iterator<String> params = this.param(name).iterator();
            if (params.hasNext()) {
                value = params.next();
            } else {
                value = def;
            }
            return value;
        }
    }
}
