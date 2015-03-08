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
package org.takes;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * HTTP URI/HREF.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.7
 */
public final class Href {

    /**
     * URI.
     */
    private final transient String uri;

    /**
     * Params.
     */
    private final transient ConcurrentMap<String, Collection<String>> params;

    /**
     * Ctor.
     * @param txt Text of the link
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public Href(final String txt) {
        this.params = new ConcurrentHashMap<String, Collection<String>>(0);
        final URI link = URI.create(txt);
        final String query = link.getQuery();
        if (query == null) {
            this.uri = link.toString();
        } else {
            final String href = link.toString();
            this.uri = href.substring(0, href.length() - query.length() - 1);
            final String[] pairs = query.split("&");
            for (final String pair : pairs) {
                final String[] parts = pair.split("=", 2);
                final String key = Href.decode(parts[0]);
                this.params.putIfAbsent(key, new LinkedList<String>());
                this.params.get(key).add(Href.decode(parts[1]));
            }
        }
    }

    /**
     * Ctor.
     * @param link The link
     * @param map Map of params
     */
    private Href(final String link,
        final ConcurrentMap<String, Collection<String>> map) {
        this.uri = link;
        this.params = map;
    }

    @Override
    public String toString() {
        final StringBuilder text = new StringBuilder(this.uri);
        if (!this.params.isEmpty()) {
            boolean first = true;
            for (final Map.Entry<String, Collection<String>> ent
                : this.params.entrySet()) {
                for (final String value : ent.getValue()) {
                    if (first) {
                        text.append('?');
                        first = false;
                    } else {
                        text.append('&');
                    }
                    text.append(Href.encode(ent.getKey()))
                        .append('=')
                        .append(Href.encode(value));
                }
            }
        }
        return text.toString();
    }

    /**
     * Add this path to the URI.
     * @param suffix The suffix
     * @return New HREF
     */
    public Href path(final String suffix) {
        return new Href(
            new StringBuilder(this.uri).append(Href.encode(suffix)).toString(),
            this.params
        );
    }

    /**
     * Add this extra param.
     * @param key Key of the param
     * @param value The value
     * @return New HREF
     */
    public Href with(final String key, final String value) {
        final ConcurrentMap<String, Collection<String>> map =
            new ConcurrentHashMap<String, Collection<String>>(
                this.params.size() + 1
            );
        map.putAll(this.params);
        map.putIfAbsent(key, new LinkedList<String>());
        map.get(key).add(value);
        return new Href(this.uri, map);
    }

    /**
     * Without this query param.
     * @param key Key of the param
     * @return New HREF
     */
    public Href without(final String key) {
        final ConcurrentMap<String, Collection<String>> map =
            new ConcurrentHashMap<String, Collection<String>>(
                this.params.size()
            );
        map.putAll(this.params);
        map.remove(key);
        return new Href(this.uri, map);
    }

    /**
     * Encode into URL.
     * @param txt Text
     * @return Encoded
     */
    private static String encode(final String txt) {
        try {
            return URLEncoder.encode(
                txt, Charset.defaultCharset().name()
            );
        } catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Decode from URL.
     * @param txt Text
     * @return Decoded
     */
    private static String decode(final String txt) {
        try {
            return URLDecoder.decode(
                txt, Charset.defaultCharset().name()
            );
        } catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
