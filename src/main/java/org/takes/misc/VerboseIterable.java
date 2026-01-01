/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.misc;

import java.util.Iterator;
import org.cactoos.Text;

/**
 * Iterable decorator that provides verbose error messages when elements are accessed.
 *
 * <p>This decorator wraps an existing iterable and provides custom error messages
 * when iteration fails or when attempting to access elements that don't exist.
 * It delegates to VerboseIterator for the actual verbose error handling during
 * iteration operations.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @param <T> Type of item
 * @since 0.10
 */
public final class VerboseIterable<T> implements Iterable<T> {

    /**
     * Original iterator.
     */
    private final Iterable<T> origin;

    /**
     * Error message when running out of items.
     */
    private final Text error;

    /**
     * Ctor.
     * @param iter Original iterator
     * @param msg Error message
     */
    public VerboseIterable(final Iterable<T> iter, final Text msg) {
        this.origin = iter;
        this.error = msg;
    }

    @Override
    public Iterator<T> iterator() {
        return new VerboseIterator<>(this.origin.iterator(), this.error);
    }
}
