/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Map view backed by a varargs entry array.
 * @since 2.0
 */
@SuppressWarnings("PMD.ArrayIsStoredDirectly")
final class PairsMap
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
