/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.takes.Response;

/**
 * HTTP status of the Response.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 1.22
 */
public interface RsStatus extends Response {

    /**
     * Get status.
     * @return Status
     * @throws IOException If fails
     */
    int status() throws IOException;

    /**
     * Request decorator, for HTTP URI query parsing.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @since 1.22
     */
    @EqualsAndHashCode(callSuper = true)
    final class Base extends RsWrap implements RsStatus {

        /**
         * HTTP status line pattern.
         */
        private static final Pattern LINE = Pattern.compile(
            "HTTP/[0-9.]+\\s+([0-9]+)\\s+.*"
        );

        /**
         * Ctor.
         * @param res Original response
         */
        public Base(final Response res) {
            super(res);
        }

        @Override
        public int status() throws IOException {
            final String line = this.head().iterator().next();
            final Matcher matcher = RsStatus.Base.LINE.matcher(line);
            if (!matcher.matches()) {
                throw new IllegalArgumentException(
                    String.format(
                        "Invalid status line: '%s'", line
                    )
                );
            }
            return Integer.parseInt(matcher.group(1));
        }
    }
}
