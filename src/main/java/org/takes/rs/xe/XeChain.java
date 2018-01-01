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
package org.takes.rs.xe;

import java.io.IOException;
import java.util.Arrays;
import lombok.EqualsAndHashCode;
import org.takes.Scalar;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Chain of sources.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class XeChain extends XeWrap {

    /**
     * Ctor.
     * @param src Sources
     */
    public XeChain(final XeSource... src) {
        this(Arrays.asList(src));
    }

    /**
     * Ctor.
     * @param items Sources
     */
    public XeChain(final Iterable<XeSource> items) {
        this(
            new Scalar<Iterable<XeSource>>() {
                @Override
                public Iterable<XeSource> get() {
                    return items;
                }
            }
        );
    }

    /**
     * Ctor.
     * @param items Sources
     * @since 1.5
     */
    public XeChain(final Scalar<Iterable<XeSource>> items) {
        super(
            new XeSource() {
                @Override
                public Iterable<Directive> toXembly() throws IOException {
                    final Directives dirs = new Directives();
                    for (final XeSource src : items.get()) {
                        dirs.push().append(src.toXembly()).pop();
                    }
                    return dirs;
                }
            }
        );
    }

}
