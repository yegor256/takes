/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;
import org.takes.rq.RqHref;

/**
 * Pass that selects an authentication mechanism based on a request parameter flag.
 * This implementation examines a specific request parameter and delegates
 * authentication to the appropriate pass based on pattern matching.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode
public final class PsByFlag implements Pass {

    /**
     * Name of the request parameter to examine for pass selection.
     */
    private final String flag;

    /**
     * Map of patterns to passes for authentication delegation.
     */
    private final Map<Pattern, Pass> passes;

    /**
     * Ctor.
     * @param pairs Map entries
     * @since 0.5.1
     */
    public PsByFlag(final PsByFlag.Pair... pairs) {
        this(PsByFlag.class.getSimpleName(), pairs);
    }

    /**
     * Ctor.
     * @param map Map
     */
    public PsByFlag(final Map<Pattern, Pass> map) {
        this(PsByFlag.class.getSimpleName(), map);
    }

    /**
     * Ctor.
     * @param flg Flag
     * @param pairs Map entries
     * @since 0.5.1
     */
    public PsByFlag(final String flg, final PsByFlag.Pair... pairs) {
        this(flg, PsByFlag.asMap(pairs));
    }

    /**
     * Ctor.
     * @param flg Flag
     * @param map Map
     */
    public PsByFlag(final String flg, final Map<Pattern, Pass> map) {
        this.flag = flg;
        this.passes = Collections.unmodifiableMap(map);
    }

    @Override
    public Opt<Identity> enter(final Request req) throws Exception {
        final Iterator<String> flg = new RqHref.Base(req).href()
            .param(this.flag).iterator();
        Opt<Identity> user = new Opt.Empty<>();
        if (flg.hasNext()) {
            final String value = flg.next();
            for (final Map.Entry<Pattern, Pass> ent : this.passes.entrySet()) {
                if (ent.getKey().matcher(value).matches()) {
                    user = ent.getValue().enter(req);
                    break;
                }
            }
        }
        return user;
    }

    @Override
    public Response exit(final Response response,
        final Identity identity) {
        return response;
    }

    /**
     * Convert entries to map.
     * @param entries Entries
     * @return Map
     */
    @SafeVarargs
    private static Map<Pattern, Pass> asMap(
        final Map.Entry<Pattern, Pass>... entries) {
        final Map<Pattern, Pass> map = new HashMap<>(entries.length);
        for (final Map.Entry<Pattern, Pass> ent : entries) {
            map.put(ent.getKey(), ent.getValue());
        }
        return map;
    }

    /**
     * Pair of values.
     * @since 0.1
     */
    public static final class Pair
        extends AbstractMap.SimpleEntry<Pattern, Pass> {
        /**
         * Serialization marker.
         */
        private static final long serialVersionUID = 7362482770166663015L;

        /**
         * Ctor.
         * @param key Key
         * @param pass Pass
         */
        public Pair(final String key, final Pass pass) {
            this(Pattern.compile(Pattern.quote(key)), pass);
        }

        /**
         * Ctor.
         * @param key Key
         * @param pass Pass
         */
        public Pair(final Pattern key, final Pass pass) {
            super(key, pass);
        }
    }

}
