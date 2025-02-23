/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Take;
import org.takes.rs.RsWithHeader;

/**
 * Take with an extra header.
 *
 * <p>This take wraps all responses of another take, adding
 * an extra header to them, through {@link RsWithHeader}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.11
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkWithHeader extends TkWrap {

    /**
     * Ctor.
     * @param take Original
     * @param key Header
     * @param value Value
     */
    public TkWithHeader(final Take take, final String key, final String value) {
        this(take, String.format("%s: %s", key, value));
    }

    /**
     * Ctor.
     * @param take Original
     * @param header Header
     */
    public TkWithHeader(final Take take, final String header) {
        super(
            req -> new RsWithHeader(take.act(req), header)
        );
    }

}
