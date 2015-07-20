/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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

/**
 * User.
 * @author Georgy Vlasov (wlasowegor@gmail.com)
 * @version $Id$
 * @since 0.22
 */
public interface User {
    /**
     * Login.
     * @return Identity
     */
    String login();

    /**
     * Password.
     * @return Password
     */
    String password();

    /**
     * Identity.
     * @return Identity
     */
    Identity identity();

    final class Default implements User {

        /**
         * Login.
         */
        private final String login;

        /**
         * Password.
         */
        private final String password;

        /**
         * Identity.
         */
        private final Identity identity;

        /**
         * Public ctor.
         * @param username Login
         * @param pwd Password
         * @param ident Identity
         */
        public Default(final String username, final String pwd,
            final Identity ident) {
            this.login = username;
            this.password = pwd;
            this.identity = ident;
        }

        /**
         * Login.
         * @return Login
         */
        @Override
        public String login() {
            return this.login;
        }

        /**
         * Password.
         * @return Password
         */
        @Override
        public String password() {
            return this.password;
        }

        /**
         * Identity.
         * @return Identity
         */
        @Override
        public Identity identity() {
            return this.identity;
        }
    }
}
