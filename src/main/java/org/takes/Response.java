/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes;

import java.io.InputStream;

/**
 * HTTP response.
 *
 * <p>{@link Response} interface is an abstraction of a HTTP
 * response, that consists of a few headers and a body. To construct
 * a response, use one of the composable decorators from
 * {@link org.takes.rs} package. For example, this code will create
 * a response with HTML inside:
 *
 * <pre> final Response response = new RsWithHeader(
 *   new RsWithBody(
 *     new RsWithStatus(200),
 *     "hello, world!"
 *   ),
 *   "Content-Type", "text/html"
 * );</pre>
 *
 * <p>The implementations of this interface may require that
 * {@link Response#head()} method has to be invoked before reading from the
 * {@code InputStream} obtained from the {@link Response#body()} method,
 * but they must NOT require that the {@link InputStream} has to be read
 * from before the {@link Response#head()} method invocation.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @see Take
 * @see org.takes.rs.RsWithBody
 * @see org.takes.rs.RsWithHeader
 * @see <a href="http://www.yegor256.com/2015/02/26/composable-decorators.html">
 *     Composable Decorators vs. Imperative Utility Methods</a>
 * @since 0.1
 */
public interface Response extends Head, Body {
}
