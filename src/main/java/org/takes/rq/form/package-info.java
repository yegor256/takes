/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * This package contains implementations of the interface
 * {@link org.takes.rq.RqForm}.
 *
 * <p>{@link org.takes.rq.RqForm} implementations can parse FORM data in
 * {@code application/x-www-form-urlencoded} format (RFC 1738) from
 * {@link org.takes.Request} objects.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * <p>Current implementations:
 * {@link org.takes.rq.form.RqFormBase} base implementation;
 * {@link org.takes.rq.form.RqFormFake} implementation that is useful for
 * testing purposes. You can add your parameters in constructor;
 * {@link org.takes.rq.form.RqFormSmart} decorator with extra features:
 * Get a single parameter and, if the parameter doesn't exist, use default value or
 * throw an HTTP exception.
 *
 * <p>Also please look at usage examples:
 *
 * <pre>
 * {@code
 * final String body = "alpha=a+b+c&beta=%20Yes%20";
 * final RqForm base = new RqFormBase(
 *     new RqBuffered(
 *         new RqFake(
 *             Arrays.asList(
 *                 "GET /h?a=3",
 *                 "Host: www.example.com",
 *                 String.format(
 *                     "Content-Length: %d",
 *                     body.getBytes().length
 *                 )
 *             ),
 *             body
 *         )
 *     )
 * );
 * }
 * </pre>
 * Create fake form with parameter: "param=value"
 * <pre>
 * {@code
 * final RqForm final = new RqFormFake(
 *     new RqFake(),
 *     "param",
 *     "value"
 * );
 * }
 * </pre>
 * Get "alpha" value form parameters:
 * <pre>
 * {@code
 * new RqFormSmart(base).single("alpha")
 * }
 * </pre>
 * @since 0.33
 */
package org.takes.rq.form;
