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
package org.takes.ts;

import java.io.IOException;
import java.io.InputStream;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Take;
import org.takes.Takes;
import org.takes.rq.RqQuery;
import org.takes.rs.RsWithBody;
import org.takes.tk.TkFixed;

/**
 * Takes reading resources from classpath.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = "prefix")
public final class TsClasspath implements Takes {

    /**
     * Prefix.
     */
    private final transient String prefix;

    /**
     * Ctor.
     * @param pfx Prefix
     */
    public TsClasspath(final String pfx) {
        this.prefix = pfx;
    }

    @Override
    public Take take(final Request request) throws IOException {
        final String name = String.format(
            "%s%s", this.prefix,
            new RqQuery(request).query().getPath()
        );
        final InputStream input = this.getClass().getResourceAsStream(name);
        if (input == null) {
            throw new Takes.NotFoundException(
                String.format("%s not found in classpath", name)
            );
        }
        try {
            return new TkFixed(
                new RsWithBody(new RqQuery(request).query().getPath())
            );
        } finally {
            input.close();
        }
    }

}
