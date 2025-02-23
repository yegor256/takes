/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.FkEncoding;
import org.takes.facets.fork.RsFork;
import org.takes.rs.RsGzip;

/**
 * Take that compresses responses with GZIP.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.10
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkGzip extends TkWrap {

    /**
     * Ctor.
     * @param take Original take
     */
    public TkGzip(final Take take) {
        super(
            req -> {
                final Response response = take.act(req);
                return new RsFork(
                    req,
                    new FkEncoding("gzip", new RsGzip(response)),
                    new FkEncoding("", response)
                );
            }
        );
    }

}
