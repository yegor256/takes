/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.cookies;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Lowered;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;
import org.cactoos.text.UncheckedText;
import org.takes.Request;
import org.takes.misc.VerboseIterable;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqWrap;

/**
 * HTTP cookies parsing.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
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
            final String value = map.getOrDefault(
                new UncheckedText(
                    new Lowered(key.toString())
                ).asString(),
                ""
            );
            final Iterable<String> iter;
            if (value.isEmpty()) {
                iter = new VerboseIterable<>(
                    Collections.emptyList(),
                    new FormattedText(
                        "There are no Cookies by name \"%s\" among %d others: %s",
                        key, map.size(), map.keySet()
                    )
                );
            } else {
                iter = new VerboseIterable<>(
                    Collections.singleton(value),
                    new FormattedText(
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
                        new UncheckedText(
                            new Lowered(new Trimmed(new TextOf(parts[0])))
                        ).asString();
                    if (parts.length > 1 && !parts[1].isEmpty()) {
                        map.put(
                            key,
                            new UncheckedText(
                                new Trimmed(new TextOf(parts[1]))
                            ).asString()
                        );
                    } else {
                        map.remove(key);
                    }
                }
            }
            return map;
        }
    }
}
