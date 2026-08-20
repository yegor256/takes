/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.SortedSet;

/**
 * Lazy collection that parses the source text only when iterated.
 * @since 2.0
 */
final class Bag extends AbstractCollection<MediaType> {

    /**
     * Source text.
     */
    private final String src;

    /**
     * Cached parsed set.
     */
    private SortedSet<MediaType> cached;

    /**
     * Ctor.
     * @param src Source text
     */
    Bag(final String src) {
        this.src = src;
    }

    @Override
    public Iterator<MediaType> iterator() {
        return this.parsed().iterator();
    }

    @Override
    public int size() {
        return this.parsed().size();
    }

    private SortedSet<MediaType> parsed() {
        if (this.cached == null) {
            this.cached = MediaTypes.parse(this.src);
        }
        return this.cached;
    }
}
