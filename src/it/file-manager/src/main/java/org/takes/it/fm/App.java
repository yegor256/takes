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
import org.takes.Request;
import org.takes.Take;
import org.takes.Takes;
import org.takes.http.FtBasic;
import org.takes.http.FtDaemon;
import org.takes.rq.RqRegex;
import org.takes.tk.TkHTML;
import org.takes.tk.TkRedirect;
import org.takes.ts.TsRegex;

/**
 * App.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class App implements Takes {

    /**
     * Home.
     */
    private final transient File home;

    /**
     * Ctor.
     * @param dir Home dir
     */
    public App(final File dir) {
        this.home = dir;
    }

    /**
     * Entry point.
     * @param args Arguments
     * @throws IOException If fails
     */
    public static void main(final String... args) throws IOException {
        new FtDaemon(
            new FtBasic(
                new App(new File(System.getProperty("user.dir")))
            )
        ).listen(Integer.parseInt(args[0]));
    }

    @Override
    public Take take(final Request request) throws IOException {
        return new TsRegex()
            .with("/", new TkRedirect("/f"))
            .with("/about", new TkHTML(App.class.getResource("about.html")))
            .with("/robots.txt", "")
            .with(
                "/f(.*)",
                new TsRegex.Fast() {
                    @Override
                    public Take take(final RqRegex req) {
                        return new TkDir(App.this.home, req.matcher().group(1));
                    }
                }
            )
            .take(request);
    }
}
