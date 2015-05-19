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
package org.takes.facets.auth.social;

import lombok.EqualsAndHashCode;
import org.takes.misc.Href;
import org.takes.rs.xe.XeLink;
import org.takes.rs.xe.XeSource;
import org.takes.rs.xe.XeWrap;

/**
 * Xembly source to create a LINK to Twitter OAuth page.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Igor Khvostenkov (ikhvostenkov@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
public final class XeTwitterLink extends XeWrap {

    /**
     * Ctor.
     * @param token Twitter token
     */
    public XeTwitterLink(final CharSequence token) {
        super(XeTwitterLink.make("takes:twitter", token));
    }

    /**
     * Generate link.
     * @param rel Related
     * @param token Twitter token
     * @return Source
     */
    private static XeSource make(final CharSequence rel,
        final CharSequence token) {
        return new XeLink(
            rel,
            new Href("https://api.twitter.com/oauth/authorize")
                .with("oauth_token", token)
        );
    }
}
