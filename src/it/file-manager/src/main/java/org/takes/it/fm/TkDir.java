/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.it.fm;

import java.io.File;
import org.takes.Response;
import org.takes.facets.fork.RqRegex;
import org.takes.facets.fork.TkRegex;

/**
 * Directory take.
 *
 * @since 0.1
 */
final class TkDir implements TkRegex {

    /**
     * Home.
     */
    private final File home;

    /**
     * Ctor.
     * @param dir Home
     */
    TkDir(final File dir) {
        this.home = dir;
    }

    @Override
    public Response act(final RqRegex req) {
        return new RsPage(
            "/dir.xsl",
            new Items(new File(this.home, req.matcher().group(1)))
        );
    }

}
