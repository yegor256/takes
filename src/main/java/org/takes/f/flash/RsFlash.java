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
package org.takes.f.flash;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import lombok.EqualsAndHashCode;
import org.takes.Response;
import org.takes.rs.RsWithCookie;

/**
 * Forwarding response.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = false, of = "origin")
public final class RsFlash implements Response {

    /**
     * Original response.
     */
    private final transient Response origin;

    /**
     * Ctor.
     * @param msg Message to show
     */
    public RsFlash(final String msg) {
        this(msg, Level.INFO);
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
        this.origin = new RsWithCookie(cookie, msg);
    }

    @Override
    public List<String> head() throws IOException {
        return this.origin.head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.origin.body();
    }
}
