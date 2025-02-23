/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.Scalar;
import org.takes.Response;
import org.takes.rs.RsText;

/**
 * Take with fixed response.
 *
 * <p>This class always returns the same response, provided via
 * constructor and encapsulated.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkFixed extends TkWrap {

    /**
     * Ctor.
     * @param text Response text
     * @since 0.23
     */
    public TkFixed(final String text) {
        this(new RsText(text));
    }

    /**
     * Ctor.
     * @param res Response
     * @since 1.4
     */
    public TkFixed(final Scalar<Response> res) {
        super(
            req -> res.value()
        );
    }

    /**
     * Ctor.
     * @param res Response
     */
    public TkFixed(final Response res) {
        super(
            req -> res
        );
    }

}
