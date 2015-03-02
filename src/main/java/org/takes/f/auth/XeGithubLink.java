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
package org.takes.f.auth;

import java.io.IOException;
import java.net.URLEncoder;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.rq.RqURI;
import org.takes.rs.xe.XeLink;
import org.takes.rs.xe.XeSource;
import org.xembly.Directive;

/**
 * Xembly source to create a LINK to Github OAuth page.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = "link")
public final class XeGithubLink implements XeSource {

    /**
     * Link to use.
     */
    private final transient XeSource link;

    /**
     * Ctor.
     * @param req Request
     * @param app Github application ID
     * @throws IOException If fails
     */
    public XeGithubLink(final Request req, final String app) throws IOException {
        this(req, app, "takes:github", PsByFlag.class.getSimpleName());
    }

    /**
     * Ctor.
     * @param req Request
     * @param app Github application ID
     * @param rel Related
     * @param flag Flag to add
     * @throws IOException If fails
     */
    public XeGithubLink(final Request req, final String app, final String rel,
        final String flag) throws IOException {
        final StringBuilder uri = new StringBuilder(
            new RqURI(req).uri().toString()
        );
        if (uri.toString().contains("?")) {
            uri.append('&');
        } else {
            uri.append('?');
        }
        uri.append(flag).append('=').append(PsGithub.class.getSimpleName());
        this.link = new XeLink(
            rel,
            String.format(
                "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s",
                URLEncoder.encode(app, "UTF-8"),
                URLEncoder.encode(uri.toString(), "UTF-8")
            )
        );
    }

    @Override
    public Iterable<Directive> toXembly() throws IOException {
        return this.link.toXembly();
    }
}
