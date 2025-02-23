/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes;

/**
 * HTTP request.
 *
 * <p>An object implementing this interface can be "parsed" using one
 * of the decorators available in {@link org.takes.rq} package. For example,
 * in order to fetch a query parameter you can use
 * {@link org.takes.rq.RqHref}:
 *
 * <pre> final Iterable&lt;String&gt; params =
 *   new RqHref(request).href().param("name");</pre>
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @see Response
 * @see Take
 * @see org.takes.facets.fork.RqRegex
 * @see org.takes.rq.RqHref
 * @see <a href="http://www.yegor256.com/2015/02/26/composable-decorators.html">
 *     Composable Decorators vs. Imperative Utility Methods</a>
 * @since 0.1
 */
public interface Request extends Head, Body {
}
