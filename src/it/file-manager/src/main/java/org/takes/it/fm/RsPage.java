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
package org.takes.it.fm;

import java.io.File;
import java.io.IOException;
import java.lang.Iterable;
import java.lang.Override;
import java.lang.UnsupportedOperationException;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsText;
import org.takes.rs.RsXSLT;
import org.takes.rs.xe.RsXembly;
import org.xembly.Directive;

/**
 * Response with a page.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
final class RsPage implements Response {

    /**
     * Response.
     */
    private final transient Response origin;

    /**
     * Ctor.
     * @param sources Xembly sources
     */
    RsPage(final RsXembly.Source... sources) {
        this.origin = new RsXembly(
            Iterables.concat(
                Arrays.asList(
                    new XeRoot("page"),
                    new XeMillis(false),
                ),
                Arrays.asList(sources)
                Arrays.asList(
                    new XeMillis(true),
                )
            )

        );
    }

    @Override
    public Response print() throws IOException {
        return this.origin.print();
    }

}
