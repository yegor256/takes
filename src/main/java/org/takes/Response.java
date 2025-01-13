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
