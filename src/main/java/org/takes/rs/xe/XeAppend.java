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

import java.util.Arrays;
import lombok.EqualsAndHashCode;
import org.cactoos.Scalar;
import org.cactoos.scalar.IoChecked;
import org.xembly.Directives;

/**
 * Xembly source to append something to an existing element.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class XeAppend extends XeWrap {

    /**
     * Ctor.
     * @param target Name of XML element
     * @param value Value to set
     */
    public XeAppend(final CharSequence target, final CharSequence value) {
        this(target, new XeDirectives(new Directives().set(value.toString())));
    }

    /**
     * Ctor.
     * @param target Name of XML element
     * @param src Source
     */
    public XeAppend(final CharSequence target, final XeSource... src) {
        this(target, Arrays.asList(src));
    }

    /**
     * Ctor.
     * @param target Name of XML element
     * @param src Source
     * @since 0.13
     */
    public XeAppend(final CharSequence target, final Iterable<XeSource> src) {
        super(
            () -> new Directives().add(target.toString()).append(
                new XeChain(src).toXembly()
            )
        );
    }

    /**
     * Ctor.
     * @param target Name of XML element
     * @param src Source
     * @since 1.4
     */
    public XeAppend(final CharSequence target, final Scalar<XeSource> src) {
        super(
            () -> new Directives().add(target.toString()).append(
                new IoChecked<>(src).value().toXembly()
            )
        );
    }

}
