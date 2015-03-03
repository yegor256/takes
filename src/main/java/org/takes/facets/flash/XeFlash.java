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
package org.takes.facets.flash;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.rq.RqHeaders;
import org.takes.rs.xe.XeSource;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Xembly source to show flash message in XML.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "req", "header" })
public final class XeFlash implements XeSource {

    /**
     * Request.
     */
    private final transient Request req;

    /**
     * Header name.
     */
    private final transient String header;

    /**
     * Ctor.
     * @param request Request
     * @param hdr Header name
     */
    public XeFlash(final Request request, final String hdr) {
        this.req = request;
        this.header = hdr;
    }

    @Override
    public Iterable<Directive> toXembly() throws IOException {
        final List<String> headers =
            new RqHeaders(this.req).header(this.header);
        final Directives dirs = new Directives();
        if (!headers.isEmpty()) {
            final String value = headers.get(0);
            dirs.add("flash")
                .add("message").set(value).up()
                .add("level").set(Level.INFO.toString());
        }
        return dirs;
    }
}
