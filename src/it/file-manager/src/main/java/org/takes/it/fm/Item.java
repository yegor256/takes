/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.it.fm;

import java.io.File;
import org.takes.rs.xe.XeSource;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Item to show (a file or a dir).
 *
 * @since 0.1
 */
final class Item implements XeSource {

    /**
     * File.
     */
    private final File file;

    /**
     * Ctor.
     * @param path File path
     */
    Item(final File path) {
        this.file = path;
    }

    @Override
    public Iterable<Directive> toXembly() {
        final String name = this.file.getName();
        return new Directives().add("file")
            .add("name").set(name).up()
            .add("size").set(Long.toString(this.file.length()));
    }

}
