/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.misc;

import java.util.Iterator;
import org.cactoos.Text;

/**
 * Verbose iterable.
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
