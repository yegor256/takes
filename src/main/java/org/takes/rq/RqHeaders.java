/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.EqualsAndHashCode;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Lowered;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;
import org.cactoos.text.UncheckedText;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.misc.VerboseList;

/**
 * HTTP headers parsing.
 *
 * <p>All implementations of this interface must be immutable and
 * thread-safe.</p>
 *
 * @since 0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface RqHeaders extends Request {

    /**
     * Get single header.
     *
     * @param key Header name
     * @return List of values (can be empty)
     * @throws IOException If fails
     */
    List<String> header(CharSequence key) throws IOException;

    /**
     * Get all header names.
     *
     * @return All names
     * @throws IOException If fails
     */
    Set<String> names() throws IOException;

    /**
     * Request decorator, for HTTP headers parsing.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @since 0.13.8
     */
    @EqualsAndHashCode(callSuper = true)
    final class Base extends RqWrap implements RqHeaders {
        /**
         * Ctor.
         *
         * @param req Original request
         */
        public Base(final Request req) {
            super(req);
        }

        @Override
        public List<String> header(final CharSequence key)
            throws IOException {
            final List<String> values = this.map().getOrDefault(
                new UncheckedText(
                    new Lowered(key.toString())
                ).asString(),
                Collections.emptyList()
            );
            final List<String> list;
            if (values.isEmpty()) {
                list = new VerboseList<>(
                    Collections.emptyList(),
                    new FormattedText(
                        "There are no headers by name \"%s\" among %d others: %s",
                        key,
                        this.map().size(),
                        this.map().keySet()
                    )
                );
            } else {
                list = new VerboseList<>(
                    values,
                    new FormattedText(
                        "There are only %d headers by name \"%s\"",
                        values.size(),
                        key
                    )
                );
            }
            return list;
        }

        @Override
        public Set<String> names() throws IOException {
            return this.map().keySet();
        }

        /**
         * Parse them all in a map.
         *
         * @return Map of them
         * @throws IOException If fails
         */
        @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
        private Map<String, List<String>> map() throws IOException {
            final Iterator<String> head = this.head().iterator();
            if (!head.hasNext()) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    "A valid request must contain at least one line in the head"
                );
            }
            head.next();
            final Map<String, List<String>> map = new HashMap<>(0);
            int pos = 1;
            while (head.hasNext()) {
                final String line = head.next();
                final String[] parts = line.split(":", 2);
                if (parts.length < 2) {
                    throw new HttpException(
                        HttpURLConnection.HTTP_BAD_REQUEST,
                        String.format(
                            "Invalid HTTP header on line #%d: \"%s\"",
                            pos, line
                        )
                    );
                }
                final String key = new UncheckedText(
                    new Lowered(new Trimmed(new TextOf(parts[0])))
                ).asString();
                if (!map.containsKey(key)) {
                    map.put(key, new LinkedList<>());
                }
                map.get(key).add(
                    new UncheckedText(
                        new Trimmed(new TextOf(parts[1]))
                    ).asString()
                );
                pos += 1;
            }
            return map;
        }
    }

    /**
     * Smart decorator, with extra features.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @since 0.16
     */
    @EqualsAndHashCode
    final class Smart implements RqHeaders {
        /**
         * Original.
         */
        private final RqHeaders origin;

        /**
         * Ctor.
         * @param req Original request
         */
        public Smart(final RqHeaders req) {
            this.origin = req;
        }

        /**
         * Ctor.
         * @param req Original request
         */
        public Smart(final Request req) {
            this(new RqHeaders.Base(req));
        }

        @Override
        public List<String> header(final CharSequence name)
            throws IOException {
            return this.origin.header(name);
        }

        @Override
        public Set<String> names() throws IOException {
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
         * Get single header or throw HTTP exception.
         * @param name Name of header
         * @return Value of it
         * @throws IOException If fails
         */
        public String single(final CharSequence name) throws IOException {
            final Iterator<String> params = this.header(name).iterator();
            if (!params.hasNext()) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    String.format(
                        "Header \"%s\" is mandatory, not found among %s",
                        name, this.names()
                    )
                );
            }
            return params.next();
        }

        /**
         * If header is present, returns the first header value.
         * If not, returns a default value.
         * @param name Name of header key
         * @param def Default value
         * @return Header Value or default value
         * @throws IOException If fails
         */
        public String single(final CharSequence name, final CharSequence def)
            throws IOException {
            final String value;
            final Iterator<String> params = this.header(name).iterator();
            if (params.hasNext()) {
                value = params.next();
            } else {
                value = def.toString();
            }
            return value;
        }

    }
}
