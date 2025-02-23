/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.net.HttpURLConnection;
import java.util.Arrays;
import lombok.ToString;
import org.takes.HttpException;
import org.takes.Take;
import org.takes.tk.TkFailure;
import org.takes.tk.TkWrap;

/**
 * Take that acts on request with specified methods only.
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.16.1
 */
@ToString(callSuper = true)
public final class TkMethods extends TkWrap {
    /**
     * Ctor.
     *
     * @param take Original take
     * @param methods Methods the take should act
     */
    public TkMethods(final Take take, final String... methods) {
        super(
            new TkFork(
                new FkMethods(Arrays.asList(methods), take),
                new FkFixed(
                    new TkFailure(
                        () -> new HttpException(
                            HttpURLConnection.HTTP_BAD_METHOD
                        )
                    )
                )
            )
        );
    }
}
