/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import lombok.EqualsAndHashCode;

/**
 * Safe back-end decorator.
 *
 * <p>This decorator wraps another {@link Back} implementation and ensures
 * that any exceptions thrown during socket processing are silently caught
 * and ignored. This is particularly useful in production environments where
 * you want to prevent a single faulty request from crashing the entire
 * server or disrupting other concurrent requests.
 *
 * <p>The decorator catches all {@link Throwable} instances, including both
 * checked and unchecked exceptions, as well as errors. This provides maximum
 * protection at the cost of potentially hiding important error information.
 * Use this decorator when server stability is more important than debugging
 * individual request failures.
 *
 * <p>Common use cases:
 * <ul>
 *   <li>Production servers that must stay running despite client errors</li>
 *   <li>Load testing scenarios where individual failures are expected</li>
 *   <li>Wrapper around other back-ends that might throw unexpected exceptions</li>
 * </ul>
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
