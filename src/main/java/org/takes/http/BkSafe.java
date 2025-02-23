/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import lombok.EqualsAndHashCode;

/**
 * Safe back-end.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class BkSafe extends BkWrap {

    /**
     * Ctor.
     * @param back Original back
     */
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    public BkSafe(final Back back) {
        super(socket -> {
            try {
                back.accept(socket);
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final Throwable ignored) {
            }
        });
    }

}
