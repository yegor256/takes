/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.Scalar;
import org.cactoos.scalar.IoChecked;

/**
 * Take that always throws an exception when invoked.
 *
 * <p>This {@link Take} implementation is designed to always fail with
 * a specified exception when processing any request. It's primarily used
 * for testing error handling, simulating failure scenarios, and creating
 * intentional failure points in application routing.
 *
 * <p>The take supports multiple types of exceptions including runtime
 * exceptions, checked IOException instances, and lazy exception suppliers
 * for dynamic error generation. This flexibility enables comprehensive
 * testing of different failure modes and exception handling paths.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Default failure with generic message
 * new TkFailure();
 *
 * // Custom failure message
 * new TkFailure("Database connection failed");
 *
 * // Specific runtime exception
 * new TkFailure(new IllegalArgumentException("Invalid input"));
 *
 * // IOException for testing I/O error handling
 * new TkFailure(new IOException("Network timeout"));
 *
 * // Dynamic exception generation
 * new TkFailure(() -> new IOException("Current time: " + System.currentTimeMillis()));
 * }</pre>
 *
 * <p>Common use cases include:
 * <ul>
 *   <li>Unit testing exception handling and error recovery</li>
 *   <li>Integration testing of error reporting systems</li>
 *   <li>Load testing with controlled failure injection</li>
 *   <li>Development environment error simulation</li>
 *   <li>Circuit breaker pattern testing</li>
 *   <li>Fallback mechanism validation</li>
 *   <li>Error monitoring and alerting system testing</li>
 * </ul>
 *
 * <p>The take immediately throws the configured exception upon any request
 * without processing request content or generating any response. This ensures
 * consistent failure behavior regardless of request parameters or timing.
 *
 * <p>Different constructors support various exception types to match specific
 * testing requirements, from simple runtime exceptions to complex I/O errors
 * and dynamic exception generation scenarios.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkFailure extends TkWrap {

    /**
     * Ctor.
     * Creates a failure take with default "Intentional failure" message.
     */
    public TkFailure() {
        this("Intentional failure");
    }

    /**
     * Ctor.
     * @param err Error message to include in thrown exception
     */
    public TkFailure(final String err) {
        this(new IllegalStateException(err));
    }

    /**
     * Ctor.
     * @param err Runtime exception to throw on each request
     */
    public TkFailure(final RuntimeException err) {
        super(
            request -> {
                throw err;
            }
        );
    }

    /**
     * Ctor.
     * @param err Scalar supplier of IOException for dynamic exception generation
     * @since 1.4
     */
    public TkFailure(final Scalar<IOException> err) {
        super(
            request -> {
                throw new IoChecked<>(err).value();
            }
        );
    }

    /**
     * Ctor.
     * @param err IOException to throw on each request
     * @since 0.27
     */
    public TkFailure(final IOException err) {
        super(
            request -> {
                throw err;
            }
        );
    }

}
