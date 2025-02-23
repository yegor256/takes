/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.iterable.Filtered;
import org.cactoos.scalar.Not;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Lowered;
import org.cactoos.text.StartsWith;
import org.takes.Response;

/**
 * Response decorator, without a header.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.9
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsWithoutHeader extends RsWrap {

    /**
     * Ctor.
     * @param res Original response
     * @param name Header name
     */
    public RsWithoutHeader(final Response res, final CharSequence name) {
        super(
            new ResponseOf(
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
                    res.head()
                ),
                res::body
            )
        );
    }
}
