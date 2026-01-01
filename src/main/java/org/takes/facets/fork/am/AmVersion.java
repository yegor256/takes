/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork.am;

/**
 * Matches specified version.
 *
 * @since 1.7.2
 */
public final class AmVersion implements AgentMatch {

    /**
     * User agent name.
     */
    private final String agent;

    /**
     * Version matcher.
     */
    private final VersionMatch version;

    /**
     * Ctor.
     * @param agent Uer agent name.
     * @param version Version matcher.
     */
    public AmVersion(final String agent, final VersionMatch version) {
        this.agent = agent;
        this.version = version;
    }

    @Override
    @SuppressWarnings("PMD.ConfusingTernary")
    public boolean matches(final String token) {
        boolean result = true;
        final String[] parts = token.split("/");
        if (parts.length <= 1) {
            result = false;
        } else if (!this.agent.equalsIgnoreCase(parts[0])) {
            result = false;
        } else if (!this.version.matches(majorVersion(parts[1]))) {
            result = false;
        }
        return result;
    }

    /**
     * Parse major version number.
     * @param part Token part.
     * @return Parsed major version number.
     */
    private static int majorVersion(final String part) {
        return Integer.parseInt(part.split("\\.")[0]);
    }

    /**
     * Matches specified version when it greater than specified one.
     *
     * @since 1.7.2
     */
    public static final class VmGreater implements VersionMatch {

        /**
         * Version.
         */
        private final int ver;

        /**
         * Ctor.
         * @param ver Version.
         */
        public VmGreater(final int ver) {
            this.ver = ver;
        }

        @Override
        public boolean matches(final int version) {
            return version > this.ver;
        }
    }
}
