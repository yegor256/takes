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
package org.takes.rq.form;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.EqualsAndHashCode;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.misc.EnglishLowerCase;
import org.takes.misc.Sprintf;
import org.takes.misc.VerboseIterable;
import org.takes.rq.RqForm;
import org.takes.rq.RqPrint;
import org.takes.rq.RqWrap;

/**
 * Base implementation of {@link RqForm}.
 * @author Aleksey Popov (alopen@yandex.ru)
 * @version $Id$
 * @since 0.33
 */
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
@EqualsAndHashCode(callSuper = true)
public final class RqFormBase extends RqWrap implements RqForm {

    /**
     * Request.
     */
    private final Request req;

    /**
     * Saved map.
     */
    private final List<Map<String, List<String>>> saved;

    /**
     * Ctor.
     * @param request Original request
     */
    public RqFormBase(final Request request) {
        super(request);
        this.saved = new CopyOnWriteArrayList<>();
        this.req = request;
    }

    @Override
    public Iterable<String> param(final CharSequence key)
        throws IOException {
        final List<String> values =
            this.map().get(new EnglishLowerCase(key.toString()).string());
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
        final String body = new RqPrint(this.req).printBody();
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
            final String key = RqFormBase.decode(
                new EnglishLowerCase(parts[0].trim()).string()
            );
            if (!map.containsKey(key)) {
                map.put(key, new LinkedList<String>());
            }
            map.get(key).add(RqFormBase.decode(parts[1].trim()));
        }
        return map;
    }
}
