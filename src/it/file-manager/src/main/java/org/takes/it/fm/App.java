/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
