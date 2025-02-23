/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.misc;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.cactoos.Text;
import org.cactoos.text.UncheckedText;

/**
 * Verbose iterator.
 *
 * @param <T> Type of item
 * @since 0.10
 */
public final class VerboseIterator<T> implements Iterator<T> {

    /**
     * Original iterator.
     */
    private final Iterator<T> origin;

    /**
     * Error message when running out of items.
     */
    private final Text error;

    /**
     * Ctor.
     * @param iter Original iterator
     * @param msg Error message
     */
    public VerboseIterator(final Iterator<T> iter, final Text msg) {
        this.origin = iter;
        this.error = new UncheckedText(msg);
    }

    @Override
    public boolean hasNext() {
        return this.origin.hasNext();
    }

    @Override
    public T next() {
        if (!this.origin.hasNext()) {
            throw new NoSuchElementException(this.error.toString());
        }
        return this.origin.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("#remove()");
    }
}
