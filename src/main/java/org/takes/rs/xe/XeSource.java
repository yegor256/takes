/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs.xe;

import java.io.IOException;
import java.util.Collections;
import org.xembly.Directive;

/**
 * Source with Xembly directives.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 0.1
 */
public interface XeSource {

    /**
     * Empty.
     */
    XeSource EMPTY = () -> Collections.emptyList();

    /**
     * Get Xembly directives.
     * @return Directives
     * @throws IOException If fails
     */
    Iterable<Directive> toXembly() throws IOException;

}
