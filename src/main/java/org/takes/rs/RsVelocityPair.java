/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.util.AbstractMap;

/**
 * Pair of values.
 * @since 0.1
 */
public final class RsVelocityPair
    extends AbstractMap.SimpleEntry<CharSequence, Object> {

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = 7362489770169963015L;

    /**
     * Ctor.
     * @param key Key
     * @param obj Pass
     */
    public RsVelocityPair(final CharSequence key, final Object obj) {
        super(key, obj);
    }
}
