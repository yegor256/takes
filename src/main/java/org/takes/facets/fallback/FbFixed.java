/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import lombok.EqualsAndHashCode;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Opt;
import org.takes.tk.TkFixed;

/**
 * Fallback with a fixed response.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.13
 */
@EqualsAndHashCode(callSuper = true)
public final class FbFixed extends FbWrap {

    /**
     * Ctor.
     * @param response Response to return
     */
    public FbFixed(final Response response) {
        this(new TkFixed(response));
    }

    /**
     * Ctor.
     * @param take Take to use
     * @since 0.14
     */
    public FbFixed(final Take take) {
        super(
            req -> new Opt.Single<>(take.act(req))
        );
    }

}
