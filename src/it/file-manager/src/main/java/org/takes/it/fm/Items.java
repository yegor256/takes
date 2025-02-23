/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.it.fm;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import org.takes.rs.xe.XeChain;
import org.takes.rs.xe.XeSource;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Items to show.
 *
 * @since 0.1
 */
final class Items implements XeSource {

    /**
     * Home.
     */
    private final File home;

    /**
     * Ctor.
     * @param dir Home directory
     */
    Items(final File dir) {
        this.home = dir;
    }

    @Override
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public Iterable<Directive> toXembly() throws IOException {
        final Collection<XeSource> items = new LinkedList<>();
        final File[] files = this.home.listFiles();
        if (files != null) {
            for (final File file : files) {
                items.add(new Item(file));
            }
        }
        return new Directives().add("files").append(
            new XeChain(items).toXembly()
        );
    }
}
