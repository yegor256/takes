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

/**
 * Previous.
 *
 * <p>These classes may be useful when you want to redirect your user
 * to some location and remember what he/she wanted to see before. This
 * is especially useful during login. When the user is trying to open
 * some page, where access credentials are required, you throw
 * {@link org.takes.facets.forward.RsForward} to the home page,
 * with this class inside, with the original URL:
 *
 * <pre> if (not_logged_id) {
 *   throw new RsForward(
 *     new RsWithPrevious(
 *       new RsFlash("You must be logged in!")
 *     )
 *   );
 * }</pre>
 *
 * <p>Then, you decorate your application with
 * {@link org.takes.facets.previous.TkPrevious} and that's it.
 *
 * @since 0.10
 */
package org.takes.facets.previous;
