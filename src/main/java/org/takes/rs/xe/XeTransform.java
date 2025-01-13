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
package org.takes.rs.xe;

import java.util.Iterator;
import lombok.EqualsAndHashCode;
import org.cactoos.Func;
import org.cactoos.func.UncheckedFunc;

/**
 * Iterable to transform an iterable of some objects
 * into an iterable of Xembly sources.
 *
 * <p>Use this class to create a collection of
 * {@link XeSource} objects and pass them to,
 * for example, {@link org.takes.rs.xe.XeAppend}:
 *
 * <pre> return new RsXembly(
 *   new XeAppend(
 *     "books",
 *     new XeTransform&lt;Book&gt;(
 *       this.database.books(),
 *       new XeTransform.Func&lt;Book&gt;() {
 *         &#64;Override
 *         public XeSource transform(final Book book) {
 *           return new XeAppend(
 *             "book",
 *             new XeDirectives(
 *               new Directives()
 *                 .add("book")
 *                 .attr("isbn", book.isbn());
 *             )
 *           )
 *         }
 *       }
 *     )
 *   )
 * );</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 * @param <T> Type of item
 */
@EqualsAndHashCode
public final class XeTransform<T> implements Iterable<XeSource> {

    /**
     * Iterable of objects.
     */
    private final Iterable<T> objects;

    /**
     * Function to use for mapping.
     */
    private final Func<T, XeSource> func;

    /**
     * Ctor.
     * @param list List of objects
     * @param fnc Function
     */
    public XeTransform(final Iterable<T> list, final Func<T, XeSource> fnc) {
        this.objects = list;
        this.func = fnc;
    }

    @Override
    public Iterator<XeSource> iterator() {
        final Iterator<T> origin = this.objects.iterator();
        return new Iterator<XeSource>() {
            @Override
            public boolean hasNext() {
                return origin.hasNext();
            }

            @Override
            public XeSource next() {
                return new UncheckedFunc<>(
                    XeTransform.this.func
                ).apply(origin.next());
            }

            @Override
            public void remove() {
                origin.remove();
            }
        };
    }

}
