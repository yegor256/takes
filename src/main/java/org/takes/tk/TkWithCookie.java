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
import org.takes.facets.cookies.RsWithCookie;

/**
 * Take that headers.
 *
 * <p>This take wraps all responses of another take, adding
 * cookies to them, through {@link RsWithCookie}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkWithCookie extends TkWrap {

    /**
     * Ctor.
     * @param take Original
     * @param key Cookie name
     * @param value Cookie value
     */
    public TkWithCookie(final Take take, final String key, final String value) {
        super(
            new TkWithHeaders(
                take,
                String.format("Set-Cookie: %s=%s", key, value)
        )
        );
    }

}
