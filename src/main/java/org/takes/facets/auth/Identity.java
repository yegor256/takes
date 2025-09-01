/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.util.Collections;
import java.util.Map;

/**
 * Authenticated identity representing a user in the system.
 * This interface encapsulates user information obtained after successful
 * authentication, including a unique identifier (URN) and optional
 * properties such as name, email, or other user attributes.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 0.1
 */
public interface Identity {

    /**
     * Anonymous identity representing an unauthenticated user.
     * This constant provides a special identity for users who have
     * not been authenticated or have chosen to remain anonymous.
     */
    Identity ANONYMOUS = new Identity() {
        @Override
        public String toString() {
            return "anonymous";
        }

        @Override
        public String urn() {
            throw new UnsupportedOperationException("#urn()");
        }

        @Override
        public Map<String, String> properties() {
            throw new UnsupportedOperationException("#properties()");
        }
    };

    /**
     * URN of it, in "urn:PASS:ID" format.
     * @return URN of the user
     */
    String urn();

    /**
     * Properties of it, like name, photo, etc.
     * @return Properties
     */
    Map<String, String> properties();

    /**
     * Simple implementation of Identity interface.
     * This class provides a straightforward implementation that stores
     * a URN and an immutable map of properties.
     * @since 0.1
     */
    final class Simple implements Identity {
        /**
         * URN.
         */
        private final String name;

        /**
         * Map of properties.
         */
        private final Map<String, String> props;

        /**
         * Ctor.
         * @param urn URN of the identity
         */
        public Simple(final String urn) {
            this(urn, Collections.emptyMap());
        }

        /**
         * Ctor.
         * @param urn URN of the identity
         * @param map Map of properties
         */
        public Simple(final String urn, final Map<String, String> map) {
            this.name = urn;
            this.props = Collections.unmodifiableMap(map);
        }

        @Override
        public String urn() {
            return this.name;
        }

        @Override
        public Map<String, String> properties() {
            return Collections.unmodifiableMap(this.props);
        }
    }

}
