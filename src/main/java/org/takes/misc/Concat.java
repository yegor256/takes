/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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
import java.util.List;

/**
 * Concat iterable.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.32.1
 */
public final class Concat<T> implements Iterable<T> {

    /**
     * Internal storage to hold the elements from iterables.
     */
    private final transient List<T> storage = new LinkedList<T>();

    /**
     * To produce an iterable collection combining a and b, with order of the
     * elements in a first.
     *
     * @param aitb First iterable to concat
     * @param bitb Second iterable to conat
     */
    public Concat(final Iterable<T> aitb, final Iterable<T> bitb) {
        this.concat(aitb);
        this.concat(bitb);
    }

    /**
     * To produce an iterable collection, determined by condition, combining a
     * and b, with order of the elements in a first.
     *
     * @param aitb First iterable to concat
     * @param bitb Second iterable to conat
     * @param cond To determine which element to add in the final iterable
     */
    public Concat(final Iterable<T> aitb, final Iterable<T> bitb,
            final Condition<T> cond) {
        this.concat(aitb, cond);
        this.concat(bitb, cond);
    }

    @Override
    public Iterator<T> iterator() {
        return this.storage.iterator();
    }

    /**
     * Adding an iterable into storage with condition.
     *
     * @param itb Iterable to add
     * @param cond Condition to determine the element should be added
     */
    private void concat(final Iterable<T> itb, final Condition<T> cond) {
        final Iterator<T> itr = itb.iterator();
        while (itr.hasNext()) {
            final T element = itr.next();
            if (cond.add(element)) {
                this.storage.add(element);
            }
        }
    }

    /**
     * Adding an iterable into storage.
     *
     * @param itb Iterable to add
     */
    private void concat(final Iterable<T> itb) {
        final Iterator<T> itr = itb.iterator();
        while (itr.hasNext()) {
            this.storage.add(itr.next());
        }
    }

    public interface Condition<T> {
        /**
         * Determine if an element should be added.
         *
         * @param element The element in the iterables to examine.
         * @return True to add the element, false to skip.
         */
        boolean add(T element);
    }

}
