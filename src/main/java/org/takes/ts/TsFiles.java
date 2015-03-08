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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.takes.NotFoundException;
import org.takes.Request;
import org.takes.Take;
import org.takes.Takes;
import org.takes.rq.RqQuery;
import org.takes.rs.RsWithBody;
import org.takes.tk.TkFixed;

/**
 * Takes reading resources from directory.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = "dir")
public final class TsFiles implements Takes {

    /**
     * Directory.
     */
    private final transient File dir;

    /**
     * Ctor.
     * @param base Base directory
     */
    public TsFiles(final String base) {
        this(new File(base));
    }

    /**
     * Ctor.
     * @param base Base directory
     */
    public TsFiles(final File base) {
        this.dir = base;
    }

    @Override
    public Take route(final Request request) throws IOException {
        final File file = new File(
            this.dir,
            new RqQuery(request).query().getPath()
        );
        if (!file.exists()) {
            throw new NotFoundException(
                String.format("%s not found", file.getAbsolutePath())
            );
        }
        return new TkFixed(new RsWithBody(new FileInputStream(file)));
    }

}
