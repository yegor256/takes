/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.previous;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;
import org.takes.facets.cookies.RsWithCookie;
import org.takes.misc.Expires;
import org.takes.rs.RsWrap;

/**
 * Response decorator, with a link to previous page.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 1.10
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsPrevious extends RsWrap {

    /**
     * Ctor.
     * @param rsp Response to decorate
     * @param location The location user is trying to access
     * @throws UnsupportedEncodingException If fails to encode
     */
    public RsPrevious(final Response rsp, final String location)
        throws UnsupportedEncodingException {
        super(
            new RsWithCookie(
                rsp,
                TkPrevious.class.getSimpleName(),
                URLEncoder.encode(location, "UTF-8"),
                "Path=/",
                new Expires.Date(
                    System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1L)
                ).print()
            )
        );
    }

}
