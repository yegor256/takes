/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
 * @param <T> Type of item
 * @since 0.1
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
