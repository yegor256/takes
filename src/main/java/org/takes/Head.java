/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes;

import java.io.IOException;

/**
 * Head abstraction for {@link Request} and {@link Response}.
 * @since 2.0
 */
public interface Head {
    /**
     * All lines above the body.
     * @return List of lines
     * @throws IOException If something goes wrong
     */
    Iterable<String> head() throws IOException;
}
