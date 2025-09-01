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
 * Interface for extracting HTTP status codes from responses.
 *
 * <p>This interface provides functionality to parse and extract HTTP
 * status codes from response status lines. It includes validation
 * of status line format and proper parsing of the three-digit status
 * code according to HTTP specifications.
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
     * Response decorator for HTTP status code parsing and extraction.
     *
     * <p>This implementation parses the HTTP status line using regex
     * pattern matching to extract the three-digit status code. It validates
     * the status line format and throws appropriate exceptions for malformed
     * status lines.
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
