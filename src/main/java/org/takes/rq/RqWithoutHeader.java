/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import lombok.EqualsAndHashCode;
import org.cactoos.iterable.Filtered;
import org.cactoos.scalar.Not;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Lowered;
import org.cactoos.text.StartsWith;
import org.takes.Request;

/**
 * Request without a header (even if it was absent).
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.8
 */
@EqualsAndHashCode(callSuper = true)
public final class RqWithoutHeader extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     * @param name Header name
     */
    public RqWithoutHeader(final Request req, final CharSequence name) {
        super(
            // @checkstyle AnonInnerLengthCheck (50 lines)
            new RequestOf(
                () -> new Filtered<>(
                    header -> new Not(
                        new StartsWith(
                            new Lowered(header),
                            new FormattedText(
                                "%s:",
                                new Lowered(name.toString())
                            )
                        )
                    ).value(),
                    req.head()
                ),
                req::body
            )
        );
    }

}
