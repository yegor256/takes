/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import lombok.EqualsAndHashCode;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.takes.rs.RsText;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWrap;

/**
 * Response reporting a failure of the back-end or of the take.
 *
 * <p>When a request cannot be read, or the take fails, the client still
 * deserves an answer that tells what went wrong. This response carries
 * the given status code and the stack trace of the failure as plain
 * text.</p>
 *
 * @since 1.20
 */
@EqualsAndHashCode(callSuper = true)
final class RsFault extends RsWrap {

    /**
     * Ctor.
     *
     * @param err Failure to report
     * @param code HTTP status code
     */
    RsFault(final Throwable err, final int code) {
        super(
            new RsWithStatus(
                new RsText(new UncheckedBytes(new BytesOf(err)).asBytes()),
                code
            )
        );
    }
}
