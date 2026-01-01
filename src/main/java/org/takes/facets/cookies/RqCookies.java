/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
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
 * An interface for parsing and accessing HTTP cookies from requests.
 *
 * <p>This interface provides methods to extract cookie values and names from
 * HTTP requests. It parses the Cookie header and makes individual cookies
 * accessible by name. All implementations of this interface must be immutable
 * and thread-safe.
 *
 * @since 0.14
 */
public interface RqCookies extends Request {
    /**
     * Retrieves the value of a single cookie by name.
     * @param name The cookie name to look for
     * @return An iterable of cookie values (can be empty if not found)
     * @throws IOException If cookie parsing fails
     */
    Iterable<String> cookie(CharSequence name) throws IOException;

    /**
     * Retrieves all cookie names present in the request.
     * @return An iterable of all cookie names
     * @throws IOException If cookie parsing fails
     */
    Iterable<String> names() throws IOException;

    /**
     * A request decorator that implements HTTP cookie parsing functionality.
     *
     * <p>This class decorates HTTP requests to provide cookie parsing capabilities.
     * It extracts cookies from the Cookie header, normalizes cookie names to
     * lowercase, and provides access to individual cookie values and names.
     * The class is immutable and thread-safe.
     *
     * @since 0.14
     */
    @EqualsAndHashCode(callSuper = true)
    final class Base extends RqWrap implements RqCookies {
        /**
         * Constructor that decorates the given request with cookie parsing.
         * @param req The original HTTP request to decorate
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
         * Parses all cookies into a map of name-value pairs.
         * @return A map containing all cookies with lowercased names as keys
         * @throws IOException If cookie parsing fails
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
