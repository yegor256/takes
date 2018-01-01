/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.misc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Select elements into a new iterable with given condition.
 *
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @param <T> Type of item
 * @since 0.13.8
 */
public final class Select<T> implements Iterable<T> {

    /**
     * Internal storage to hold the elements from iterables.
     */
    private final Iterable<T> list;

    /**
     * The condition to filter the element in the iterator.
     */
    private final Condition<T> condition;

    /**
     * To produce an iterable collection, determined by condition, combining a
     * and b, with order of the elements in a first.
     * @param itb Iterable to select
     * @param cond To determine which element to add in the final iterable
     */
    public Select(final Iterable<T> itb, final Condition<T> cond) {
        this.list = itb;
        this.condition = cond;
    }

    @Override
    public String toString() {
        return String.format("%s [if %s]", this.list, this.condition);
    }

    @Override
    public Iterator<T> iterator() {
        return new SelectIterator<T>(
            this.list.iterator(),
            this.condition
        );
    }

    /**
     * The select iterator to traverse the input iterables and return the
     * elements from the list with given condition.
     *
     * <p>This class is NOT thread-safe.
     *
     * @param <E> Type of item
     */
    private static final class SelectIterator<E> implements Iterator<E> {
        /**
         * The iterator to reflect the traverse state.
         */
        private final Iterator<E> iterator;
        /**
         * The condition to filter the elements in the iterator.
         */
        private final Condition<E> condition;
        /**
         * The buffer storing the objects of the iterator.
         */
        private final Queue<E> buffer;
        /**
         * Ctor. ConcatIterator traverses the element.
         * @param itr Iterator of the original iterable
         * @param cond Condition to filter out elements
         */
        SelectIterator(final Iterator<E> itr, final Condition<E> cond) {
            this.buffer = new LinkedList<E>();
            this.condition = cond;
            this.iterator = itr;
        }
        @Override
        public boolean hasNext() {
            if (this.buffer.isEmpty()) {
                while (this.iterator.hasNext()) {
                    final E object = this.iterator.next();
                    if (this.condition.fits(object)) {
                        this.buffer.add(object);
                        break;
                    }
                }
            }
            return !this.buffer.isEmpty();
        }
        @Override
        public E next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException(
                    "No more element with fits the select condition."
                );
            }
            return this.buffer.poll();
        }
        @Override
        public void remove() {
            throw new UnsupportedOperationException(
                "This iterable is immutable and cannot remove anything"
            );
        }
    }

}
