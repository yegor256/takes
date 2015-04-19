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
 * Transform elements in an iterable (in type T) into others (in type K).
 *
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @since 0.13.8
 */
public class Transformer<T, K> implements Iterable<K> {

    /**
     * Internal storage.
     */
    private final transient List<K> storage = new LinkedList<K>();

    /**
     * Transform elements in the supplied iterable by the action supplied.
     * @param list Iterable to be transformed
     * @param action The actual transformation implementation
     */
    public Transformer(final Iterable<T> list,
            final TransformAction<T, K> action) {
        final Iterator<T> itr = list.iterator();
        while (itr.hasNext()) {
            this.storage.add(action.transform(itr.next()));
        }
    }

    @Override
    public final Iterator<K> iterator() {
        return this.storage.iterator();
    }

}
