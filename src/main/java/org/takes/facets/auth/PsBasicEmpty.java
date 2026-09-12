/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import org.takes.misc.Opt;

/**
 * Empty implementation that always denies authentication.
 * This implementation always returns an empty identity,
 * effectively rejecting all authentication attempts.
 * @since 0.20
 */
public final class PsBasicEmpty implements PsBasic.Entry {

    /**
     * Ctor.
     */
    public PsBasicEmpty() {
        // nothing to initialize
    }

    @Override
    public Opt<Identity> enter(final String user, final String pwd) {
        return new Opt.Empty<>();
    }
}
