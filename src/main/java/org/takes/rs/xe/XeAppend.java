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
package org.takes.rs.xe;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Xembly source to append something to an existing element.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "target", "source" })
public final class XeAppend implements XeSource {

    /**
     * Target.
     */
    private final transient String target;

    /**
     * Source to add.
     */
    private final transient XeSource source;

    /**
     * Ctor.
     * @param name Name of XML element
     * @param value Value to set
     */
    public XeAppend(final String name, final String value) {
        this(name, new XeDirectives(new Directives().set(value)));
    }

    /**
     * Ctor.
     * @param name Name of XML element
     * @param src Source
     */
    public XeAppend(final String name, final XeSource... src) {
        this.target = name;
        this.source = new XeChain(src);
    }

    @Override
    public Iterable<Directive> toXembly() throws IOException {
        return new Directives().add(this.target).append(
            this.source.toXembly()
        );
    }
}
