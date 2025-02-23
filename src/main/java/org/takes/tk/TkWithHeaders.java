/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.util.Arrays;
import java.util.Collection;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Take;
import org.takes.rs.RsWithHeaders;

/**
 * Take that headers.
 *
 * <p>This take wraps all responses of another take, adding
 * headers to them, through {@link RsWithHeaders}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkWithHeaders extends TkWrap {

    /**
     * Ctor.
     * @param take Original
     * @param headers Headers
     */
    public TkWithHeaders(final Take take, final String... headers) {
        this(take, Arrays.asList(headers));
    }

    /**
     * Ctor.
     * @param take Original
     * @param headers Headers
     */
    public TkWithHeaders(final Take take, final Collection<String> headers) {
        super(
            req -> new RsWithHeaders(take.act(req), headers)
        );
    }

}
