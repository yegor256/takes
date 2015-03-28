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

package org.takes.facets.slf4j;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.takes.http.Exit;
import org.takes.http.Front;

/**
 * Logs Front.start() calls.
 *
 * <p>The class is immutable and thread-safe.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 */
@EqualsAndHashCode(of = "origin", callSuper = false)
public final class FtLogged extends LogWrap implements Front {
    /**
     * Original front.
     */
    private final transient Front origin;

    /**
     * Ctor.
     * @param front Original
     */
    public FtLogged(final Front front) {
        this(front, LogWrap.Level.TRACE);
    }

    /**
     * Ctor.
     * @param front Original
     * @param lvl Log level
     */
    public FtLogged(final Front front, final LogWrap.Level lvl) {
        super(front.getClass(), lvl);
        this.origin = front;
    }

    @Override
    public void start(final Exit exit) throws IOException {
        this.log(
            "[%s] #start(%s)",
            this.origin,
            exit
        );
        this.origin.start(exit);
    }
}
