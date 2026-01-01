/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes;

import java.io.IOException;
import java.io.InputStream;

/**
 * Body abstraction for {@link Request} and {@link Response}.
 *
 * @since 2.0
 */
public interface Body {
    /**
     * Body.
     * @return Stream with body
     * @throws IOException If something goes wrong
     */
    InputStream body() throws IOException;
}
