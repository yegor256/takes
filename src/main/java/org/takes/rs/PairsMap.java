/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.util.Map;

/**
 * Map view backed by a varargs entry array.
 * @since 2.0
 */
@SuppressWarnings("PMD.ArrayIsStoredDirectly")
final class PairsMap
    extends java.util.AbstractMap<CharSequence, Object> {

    /**
     * Source entries.
     */
    private final Map.Entry<CharSequence, Object>[] entries;

    /**
     * Ctor.
     * @param ents Entries
     */
    @SafeVarargs
    PairsMap(final Map.Entry<CharSequence, Object>... ents) {
        this.entries = ents;
    }

    @Override
    public java.util.Set<Map.Entry<CharSequence, Object>> entrySet() {
        final java.util.Set<Map.Entry<CharSequence, Object>> set =
            new java.util.LinkedHashSet<>(this.entries.length);
        for (final Map.Entry<CharSequence, Object> ent : this.entries) {
            set.add(ent);
        }
        return set;
    }
}
