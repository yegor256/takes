/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.util.AbstractMap;
import java.util.regex.Pattern;

/**
 * Pair of values.
 * @since 0.1
 */
public final class PsByFlagPair
    extends AbstractMap.SimpleEntry<Pattern, Pass> {

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = 7362482770166663015L;

    /**
     * Ctor.
     * @param key Key
     * @param pass Pass
     */
    public PsByFlagPair(final Pattern key, final Pass pass) {
        super(key, pass);
    }
}
