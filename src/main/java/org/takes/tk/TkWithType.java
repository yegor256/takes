/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Take;
import org.takes.rs.RsWithType;

/**
 * Plain text take.
 *
 * <p>This take wraps all responses of another take, adding
 * content type to them, through {@link RsWithType}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkWithType extends TkWrap {

    /**
     * Ctor.
     * @param take Original take
     * @param type Content type
     */
    public TkWithType(final Take take, final String type) {
        super(
            req -> new RsWithType(take.act(req), type)
        );
    }

}
