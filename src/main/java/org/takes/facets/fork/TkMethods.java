/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.net.HttpURLConnection;
import java.util.Collection;
import lombok.ToString;
import org.cactoos.list.ListOf;
import org.takes.HttpException;
import org.takes.Take;
import org.takes.tk.TkFailure;
import org.takes.tk.TkWrap;

/**
 * Take that acts on request with specified methods only.
 * <p>The class is immutable and thread-safe.
 * @since 0.16.1
 */
@ToString(callSuper = true)
public final class TkMethods extends TkWrap {

    /**
     * Ctor.
     * @param take Original take
     * @param methods Methods the take should act
     */
    public TkMethods(final Take take, final String... methods) {
        this(new ListOf<>(methods), take);
    }

    /**
     * Ctor.
     * @param methods Methods the take should act
     * @param take Original take
     * @since 1.24
     */
    public TkMethods(final Collection<String> methods, final Take take) {
        super(
            new TkFork(
                new FkMethods(methods, take),
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
