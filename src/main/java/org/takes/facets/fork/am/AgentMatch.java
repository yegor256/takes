/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork.am;

/**
 * Agent match.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 1.7.2
 */
public interface AgentMatch {

    /**
     * Returns true if specified token is acceptable.
     * @param token Token.
     * @return Whether specified token matches.
     */
    boolean matches(String token);
}
