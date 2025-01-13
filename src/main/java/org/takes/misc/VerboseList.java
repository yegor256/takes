/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.cactoos.Text;
import org.cactoos.text.UncheckedText;

/**
 * Verbose List that wraps OutOfBoundsException with custom message.
 *
 * @param <T> Type of item
 * @since 0.31.1
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class VerboseList<T> implements List<T> {

    /**
     * Original list.
     */
    private final List<T> origin;

    /**
     * Error message for IndexOutOfBoundsException.
     */
    private final Text message;

    /**
     * Ctor.
     * @param list Original list
     * @param msg Error message for IndexOutOfBoundsException
     */
    public VerboseList(final List<T> list, final Text msg) {
        this.origin = list;
        this.message = msg;
    }

    @Override
    public int size() {
        return this.origin.size();
    }

    @Override
    public boolean isEmpty() {
        return this.origin.isEmpty();
    }

    @Override
    public boolean contains(final Object obj) {
        return this.origin.contains(obj);
    }

    @Override
    public Iterator<T> iterator() {
        return new VerboseIterator<>(this.origin.iterator(), this.message);
    }

    @Override
    public Object[] toArray() {
        return this.origin.toArray();
    }

    @SuppressWarnings("PMD.UseVarargs")
    @Override
    public <E> E[] toArray(final E[] arr) {
        return this.origin.toArray(arr);
    }

    @Override
    public boolean add(final T item) {
        return this.origin.add(item);
    }

    @Override
    public boolean remove(final Object obj) {
        return this.origin.remove(obj);
    }

    @Override
    public boolean containsAll(final Collection<?> coll) {
        return this.origin.containsAll(coll);
    }

    @Override
    public boolean addAll(final Collection<? extends T> coll) {
        return this.origin.addAll(coll);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends T> coll) {
        try {
            return this.origin.addAll(index, coll);
        } catch (final IndexOutOfBoundsException ex) {
            throw this.wrapException(ex);
        }
    }

    @Override
    public boolean removeAll(final Collection<?> coll) {
        return this.origin.removeAll(coll);
    }

    @Override
    public boolean retainAll(final Collection<?> coll) {
        return this.origin.retainAll(coll);
    }

    @Override
    public void clear() {
        this.origin.clear();
    }

    @Override
    public T get(final int index) {
        try {
            return this.origin.get(index);
        } catch (final IndexOutOfBoundsException ex) {
            throw this.wrapException(ex);
        }
    }

    @Override
    public T set(final int index, final T element) {
        try {
            return this.origin.set(index, element);
        } catch (final IndexOutOfBoundsException ex) {
            throw this.wrapException(ex);
        }
    }

    @Override
    public void add(final int index, final T element) {
        try {
            this.origin.add(index, element);
        } catch (final IndexOutOfBoundsException ex) {
            throw this.wrapException(ex);
        }
    }

    @Override
    public T remove(final int index) {
        try {
            return this.origin.remove(index);
        } catch (final IndexOutOfBoundsException ex) {
            throw this.wrapException(ex);
        }
    }

    @Override
    public int indexOf(final Object obj) {
        return this.origin.indexOf(obj);
    }

    @Override
    public int lastIndexOf(final Object obj) {
        return this.origin.lastIndexOf(obj);
    }

    @Override
    public ListIterator<T> listIterator() {
        return this.origin.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(final int index) {
        try {
            return this.origin.listIterator(index);
        } catch (final IndexOutOfBoundsException ex) {
            throw this.wrapException(ex);
        }
    }

    @Override
    public List<T> subList(final int fridx, final int toidx) {
        try {
            return this.origin.subList(fridx, toidx);
        } catch (final IndexOutOfBoundsException ex) {
            throw this.wrapException(ex);
        }
    }

    /**
     * Wraps {@link IndexOutOfBoundsException} with custom message.
     * @param cause Original exception
     * @return Wrapped exception
     */
    private IndexOutOfBoundsException wrapException(
        final IndexOutOfBoundsException cause) {
        final IndexOutOfBoundsException exc =
            new IndexOutOfBoundsException(
                new UncheckedText(this.message).asString()
            );
        exc.initCause(cause);
        return exc;
    }
}
