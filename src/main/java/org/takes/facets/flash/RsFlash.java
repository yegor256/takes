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

import java.util.logging.Level;
import lombok.EqualsAndHashCode;
import org.takes.rs.RsWithCookie;
import org.takes.rs.RsWrap;

/**
 * Forwarding response.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = false)
public final class RsFlash extends RsWrap {

    /**
     * Ctor.
     * @param msg Message to show
     */
    public RsFlash(final String msg) {
        this(msg, Level.INFO);
    }

    /**
     * Ctor.
     * @param err Error
     */
    public RsFlash(final Throwable err) {
        this(err.getLocalizedMessage(), Level.SEVERE);
    }

    /**
     * Ctor.
     * @param msg Message
     * @param level Level
     */
    public RsFlash(final String msg, final Level level) {
        this(msg, level, RsFlash.class.getName());
    }

    /**
     * Ctor.
     * @param msg Message
     * @param level Level
     * @param cookie Cookie name
     */
    public RsFlash(final String msg, final Level level, final String cookie) {
        super(new RsWithCookie(cookie, msg));
        assert level != null;
    }

}
