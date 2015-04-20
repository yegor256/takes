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
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @since 0.13.8
 */
public final class Concat<T> implements Iterable<T> {

    /**
     * Internal storage to hold the elements from iterables.
     */
    private final transient List<T> storage = new LinkedList<T>();

    /**
     * To produce an iterable collection combining a and b, with order of the
     * elements in a first.
     * @param aitb First iterable to concat
     * @param bitb Second iterable to conat
     */
    public Concat(final Iterable<T> aitb, final Iterable<T> bitb) {
        this.concat(aitb);
        this.concat(bitb);
    }

    @Override
    public Iterator<T> iterator() {
        return this.storage.iterator();
    }

    /**
     * Adding an iterable into storage.
     * @param itb Iterable to add
     */
    private void concat(final Iterable<T> itb) {
        final Iterator<T> itr = itb.iterator();
        while (itr.hasNext()) {
            this.storage.add(itr.next());
        }
    }

}
