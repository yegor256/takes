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

import java.util.NoSuchElementException;

/**
 * Container for item of type T that can be empty.
 *
 * <p>The class is immutable and thread-safe.
 * @author I. Sokolov (happy.neko@gmail.com)
 * @version $Id$
 * @param <T> Type of item
 * @since 0.32
 * @todo #608:30min All library methods calls that may return null
 *  should be wrapped in Optional and checked for value presence
 *  with Optional.has().
 */
public final class Optional<T> {
    /**
     * Origin.
     */
    private final transient T origin;

    /**
     * Ctor.
     * @param orgn The possibly-null item to hold
     */
    public Optional(final T orgn) {
        this.origin = orgn;
    }

    /**
     * Returns the contained item.
     * @return Item instance
     */
    public T get() {
        if (this.origin == null) {
            throw new NoSuchElementException(
                "This container is empty"
            );
        } else {
            return this.origin;
        }
    }

    /**
     * Returns true if container has item inside.
     * @return True if container is not empty
     */
    public boolean has() {
        return this.origin != null;
    }
}
