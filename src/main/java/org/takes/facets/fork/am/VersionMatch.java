/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork.am;

/**
 * Version match.
 *
 * @since 1.7.2
 */
public interface VersionMatch {

    /**
     * Returns true if specified version is acceptable.
     * @param version Request
     * @return Whether specified version matches.
     */
    boolean matches(int version);

}
