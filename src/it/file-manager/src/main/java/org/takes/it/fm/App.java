/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.Exit;
import org.takes.http.FtCli;
import org.takes.tk.TkHtml;
import org.takes.tk.TkRedirect;

/**
 * App.
 *
 * @since 0.1
 */
public final class App implements Take {

    /**
     * Home.
     */
    private final File home;

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
        new FtCli(
            new App(new File(System.getProperty("user.dir"))),
            args
        ).start(Exit.NEVER);
    }

    @Override
    public Response act(final Request request) throws Exception {
        return new TkFork(
            new FkRegex("/", new TkRedirect("/f")),
            new FkRegex(
                "/about",
                new TkHtml(App.class.getResource("about.html"))
            ),
            new FkRegex("/robots.txt", ""),
            new FkRegex("/f(.*)", new TkDir(this.home))
        ).act(request);
    }
}
