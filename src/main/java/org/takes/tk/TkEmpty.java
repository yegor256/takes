/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsEmpty;

/**
 * Empty take.
 *
 * <p>This "take" always returns an instance of {@link RsEmpty}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString
public final class TkEmpty implements Take {

    @Override
    public Response act(final Request req) {
        return new RsEmpty();
    }
}
