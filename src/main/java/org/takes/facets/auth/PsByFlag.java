/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.util.AbstractMap;
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
        this("PsByFlag", pairs);
    }

    /**
     * Ctor.
     * @param map Map
     */
    public PsByFlag(final Map<Pattern, Pass> map) {
        this("PsByFlag", map);
    }

    /**
     * Ctor.
     * @param flg Flag
     * @param pairs Map entries
     * @since 0.5.1
     */
    public PsByFlag(final String flg, final PsByFlag.Pair... pairs) {
        this(flg, new PsByFlag.PairsMap(pairs));
    }

    /**
     * Ctor.
     * @param flg Flag
     * @param map Map
     */
    public PsByFlag(final String flg, final Map<Pattern, Pass> map) {
        this.flag = flg;
        this.passes = new HashMap<>(map);
    }

    @Override
    public Opt<Identity> enter(final Request req) throws Exception {
        final Iterator<String> flg = new RqHref.Base(req).href()
            .param(this.flag).iterator();
        Opt<Identity> user = new Opt.Empty<>();
        if (flg.hasNext()) {
            user = PsByFlag.find(flg.next(), this.passes, req);
        }
        return user;
    }

    @Override
    public Response exit(final Response response, final Identity identity) {
        return response;
    }

    /**
     * Find matching pass for the given value.
     * @param value Flag value
     * @param passes Available passes
     * @param req Request
     * @return Identity if found
     * @throws Exception If fails
     */
    private static Opt<Identity> find(
        final String value,
        final Map<Pattern, Pass> passes,
        final Request req
    ) throws Exception {
        Opt<Identity> user = new Opt.Empty<>();
        for (final Map.Entry<Pattern, Pass> ent : passes.entrySet()) {
            if (ent.getKey().matcher(value).matches()) {
                user = ent.getValue().enter(req);
                break;
            }
        }
        return user;
    }

    /**
     * Map view backed by a varargs entry array.
     * @since 2.0
     */
    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    private static final class PairsMap
        extends java.util.AbstractMap<Pattern, Pass> {

        /**
         * Source entries.
         */
        private final Map.Entry<Pattern, Pass>[] entries;

        /**
         * Ctor.
         * @param ents Entries
         */
        @SafeVarargs
        PairsMap(final Map.Entry<Pattern, Pass>... ents) {
            this.entries = ents;
        }

        @Override
        public java.util.Set<Map.Entry<Pattern, Pass>> entrySet() {
            final java.util.Set<Map.Entry<Pattern, Pass>> set =
                new java.util.LinkedHashSet<>(this.entries.length);
            for (final Map.Entry<Pattern, Pass> ent : this.entries) {
                set.add(ent);
            }
            return set;
        }
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
        public Pair(final Pattern key, final Pass pass) {
            super(key, pass);
        }
    }
}
