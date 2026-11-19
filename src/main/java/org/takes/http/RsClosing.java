/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import lombok.EqualsAndHashCode;
import org.takes.Response;
import org.takes.rs.RsWithHeader;
import org.takes.rs.RsWithoutHeader;
import org.takes.rs.RsWrap;

/**
 * Response announcing that the connection ends after it.
 *
 * <p>When the back-end decides not to read another request from a
 * connection, the client must learn it from the response itself, so it
 * does not send the next request on a socket that is about to close.
 * This response replaces any {@code Connection} header of the original
 * one with {@code Connection: close}.</p>
 *
 * @since 1.20
 */
@EqualsAndHashCode(callSuper = true)
final class RsClosing extends RsWrap {

    /**
     * Ctor.
     *
     * @param res Original response
     */
    RsClosing(final Response res) {
        super(
            new RsWithHeader(
                new RsWithoutHeader(res, "Connection"),
                "Connection",
                "close"
            )
        );
    }
}
