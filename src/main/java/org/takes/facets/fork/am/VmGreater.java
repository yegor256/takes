/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork.am;

/**
 * Matches specified version when it greater than specified one.
 * @since 1.7.2
 */
public final class VmGreater implements VersionMatch {

    /**
     * Version.
     */
    private final int ver;

    /**
     * Ctor.
     * @param ver Version
     */
    public VmGreater(final int ver) {
        this.ver = ver;
    }

    @Override
    public boolean matches(final int version) {
        return version > this.ver;
    }
}
