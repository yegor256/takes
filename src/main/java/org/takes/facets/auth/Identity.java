/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.facets.auth;

import java.util.Collections;
import java.util.Map;

/**
 * Authenticated identity.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 0.1
 */
public interface Identity {

    /**
     * Anonymous.
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
     * Simple identity.
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
