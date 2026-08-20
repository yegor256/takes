/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;
import org.takes.misc.Opt;

/**
 * Fake implementation of {@link PsBasic.Entry} for testing purposes.
 * This implementation returns a predefined authentication result based
 * on a boolean condition provided during construction.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.20
 */
public final class PsBasicFake implements PsBasic.Entry {

    /**
     * Should we authenticate a user?
     */
    private final boolean condition;

    /**
     * Ctor.
     * @param cond Condition
     */
    public PsBasicFake(final boolean cond) {
        this.condition = cond;
    }

    @Override
    public Opt<Identity> enter(final String usr, final String pwd) {
        final Opt<Identity> user;
        if (this.condition) {
            user = new Opt.Single<>(
                new Identity.Simple(
                    new UncheckedText(new FormattedText("urn:basic:%s", usr)).asString()
                )
            );
        } else {
            user = new Opt.Empty<>();
        }
        return user;
    }
}
