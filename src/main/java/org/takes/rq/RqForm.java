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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
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
 * @see <a href="http://www.w3.org/TR/html401/interact/forms.html">
 *     Forms in HTML</a>
 * @see org.takes.rq.RqGreedy
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface RqForm extends Request {

    /**
     * Get single parameter.
     * @param name Parameter name
     * @return List of values (can be empty)
     * @throws IOException if something fails reading parameters
     */
    Iterable<String> param(CharSequence name) throws IOException;

    /**
     * Get all parameter names.
     * @return All names
     * @throws IOException if something fails reading parameters
     */
    Iterable<String> names() throws IOException;

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
         * Saved map.
         */
        private final transient List<Map<String, List<String>>> saved;
        /**
         * Ctor.
         * @param request Original request
         */
        public Base(final Request request) {
            super(request);
            this.saved = new CopyOnWriteArrayList<>();
            this.req = request;
        }
        @Override
        public Iterable<String> param(final CharSequence key)
            throws IOException {
            final List<String> values =
                this.map().get(key.toString().toLowerCase(Locale.ENGLISH));
            final Iterable<String> iter;
            if (values == null) {
                iter = new VerboseIterable<>(
                    Collections.<String>emptyList(),
                    new Sprintf(
                        "there are no params \"%s\" among %d others: %s",
                        key, this.map().size(), this.map().keySet()
                    )
                );
            } else {
                iter = new VerboseIterable<>(
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
        public Iterable<String> names() throws IOException {
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
         * Create map of request parameters.
         * @return Parameters map or empty map in case of error.
         * @throws IOException If something fails reading or parsing body
         */
        private Map<String, List<String>> map() throws IOException {
            synchronized (this.saved) {
                if (this.saved.isEmpty()) {
                    this.saved.add(this.freshMap());
                }
                return this.saved.get(0);
            }
        }
        /**
         * Create map of request parameter.
         * @return Parameters map or empty map in case of error.
         * @throws IOException If something fails reading or parsing body
         */
        private Map<String, List<String>> freshMap() throws IOException {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new RqPrint(this.req).printBody(baos);
            final String body = new String(
                baos.toByteArray(), StandardCharsets.UTF_8
            );
            final Map<String, List<String>> map = new HashMap<>(1);
            // @checkstyle MultipleStringLiteralsCheck (1 line)
            for (final String pair : body.split("&")) {
                if (pair.isEmpty()) {
                    continue;
                }
                // @checkstyle MultipleStringLiteralsCheck (1 line)
                final String[] parts = pair.split("=", 2);
                if (parts.length < 2) {
                    throw new HttpException(
                        HttpURLConnection.HTTP_BAD_REQUEST,
                        String.format(
                            "invalid form body pair: %s", pair
                        )
                    );
                }
                final String key = RqForm.Base.decode(
                    parts[0].trim().toLowerCase(Locale.ENGLISH)
                );
                if (!map.containsKey(key)) {
                    map.put(key, new LinkedList<String>());
                }
                map.get(key).add(RqForm.Base.decode(parts[1].trim()));
            }
            return map;
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
        public Iterable<String> param(final CharSequence name)
            throws IOException {
            return this.origin.param(name);
        }
        @Override
        public Iterable<String> names() throws IOException {
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
         * @throws IOException if something fails reading parameters
         */
        public String single(final CharSequence name, final String def)
            throws IOException {
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
    /**
     * Fake RqForm accepts parameters in the constructor.
     * @author Erim Erturk (erimerturk@gmail.com)
     * @since 0.22
     */
    final class Fake implements RqForm {

        /**
         * Fake form request.
         */
        private final RqForm fake;

        /**
         * Ctor.
         * @param req Original request
         * @param params Parameters
         * @throws IOException if something goes wrong.
         */
        public Fake(final Request req, final String... params)
            throws IOException {
            this.fake = new RqForm.Base(
                new RqWithBody(req, Fake.construct(Fake.validated(params)))
            );
        }

        @Override
        public Iterable<String> param(final CharSequence name)
            throws IOException {
            return this.fake.param(name);
        }

        @Override
        public Iterable<String> names() throws IOException {
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
         * Validate parameters.
         * @param params Parameters
         * @return Validated parameters if their count is even.
         * @throws IllegalArgumentException if parameters count is odd.
         */
        private static String[] validated(final String... params) {
            if (params.length % 2 != 0) {
                throw new IllegalArgumentException(
                    "Wrong number of parameters"
                );
            }
            return params;
        }

        /**
         * Construct request body from parameters.
         * @param params Parameters
         * @return Request body
         */
        private static String construct(final String... params) {
            final StringBuilder builder = new StringBuilder();
            for (int idx = 0; idx < params.length; idx += 2) {
                builder.append(RqForm.Fake.encode(params[idx]))
                    .append('=')
                    .append(RqForm.Fake.encode(params[idx + 1]))
                    .append('&');
            }
            return builder.toString();
        }

        /**
         * Encode text.
         * @param txt Text
         * @return Encoded text
         */
        private static String encode(final CharSequence txt) {
            try {
                return URLEncoder.encode(
                    txt.toString(), Charset.defaultCharset().name()
                );
            } catch (final UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }
}
