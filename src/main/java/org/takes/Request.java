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
