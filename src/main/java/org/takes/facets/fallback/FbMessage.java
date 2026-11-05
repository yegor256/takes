/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

/**
 * Message of an exception together with the messages of all its causes.
 *
 * <p>The class is immutable and thread-safe.</p>
 *
 * @since 2.0
 */
final class FbMessage {

    /**
     * The exception.
     */
    private final Throwable origin;

    /**
     * Ctor.
     *
     * @param exp The exception
     */
    FbMessage(final Throwable exp) {
        this.origin = exp;
    }

    /**
     * Join the messages.
     *
     * @return The text
     */
    String asString() {
        final StringBuilder txt = new StringBuilder(6);
        final String localized = this.origin.getLocalizedMessage();
        if (localized == null) {
            txt.append("NULL");
        } else {
            txt.append(localized);
        }
        final Throwable cause = this.origin.getCause();
        if (cause != null) {
            txt.append("; ").append(new FbMessage(cause).asString());
        }
        return txt.toString();
    }
}
