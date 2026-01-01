/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.Text;
import org.cactoos.text.Lowered;
import org.cactoos.text.Split;
import org.cactoos.text.UncheckedText;

/**
 * Media types.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @see org.takes.facets.fork.FkTypes
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
final class MediaTypes {

    /**
     * Set of types.
     */
    private final SortedSet<MediaType> list;

    /**
     * Ctor.
     */
    MediaTypes() {
        this("");
    }

    /**
     * Ctor.
     * @param text Text to parse
     */
    MediaTypes(final String text) {
        this(MediaTypes.parse(text));
    }

    /**
     * Ctor.
     * @param types Set of types
     */
    MediaTypes(final SortedSet<MediaType> types) {
        this.list = Collections.unmodifiableSortedSet(types);
    }

    /**
     * Contains any of these types?
     * @param types Types
     * @return TRUE if any of these types are present inside this.list
     */
    public boolean contains(final MediaTypes types) {
        boolean contains = false;
        for (final MediaType type : types.list) {
            if (this.contains(type)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    /**
     * Contains this type?
     * @param type Type
     * @return TRUE if this type is present inside this.list
     */
    public boolean contains(final MediaType type) {
        boolean contains = false;
        for (final MediaType mine : this.list) {
            if (mine.matches(type)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    /**
     * Merge with this one.
     * @param types Types
     * @return Merged list
     */
    public MediaTypes merge(final MediaTypes types) {
        final SortedSet<MediaType> set = new TreeSet<>();
        set.addAll(this.list);
        set.addAll(types.list);
        return new MediaTypes(set);
    }

    /**
     * Is it empty?
     * @return TRUE if empty
     */
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    /**
     * Ctor.
     * @param text Text to parse
     * @return List of media types
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private static SortedSet<MediaType> parse(final String text) {
        final SortedSet<MediaType> list = new TreeSet<>();
        final Iterable<Text> parts = new Split(
            new UncheckedText(new Lowered(text)),
            ","
        );
        for (final Text part : parts) {
            final String name = new UncheckedText(part).asString();
            if (!name.isEmpty()) {
                list.add(new MediaType(name));
            }
        }
        return list;
    }
}
