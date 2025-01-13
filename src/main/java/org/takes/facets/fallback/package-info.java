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
 * Fallback.
 *
 * <p>Exception handing in the framework is very simple and very
 * intuitive. All you need to do is to wrap your "take" into
 * {@link org.takes.facets.fallback.TkFallback} decorator and create
 * a fallback that dispatches exceptions, for example:
 *
 * <pre> Take take = new TkFallback(
 *   original_take,
 *   new FbChain(
 *     new FbOnStatus(404, new TkHTML("page not found")),
 *     new FbOnStatus(405, new TkHTML("this method not allowed")),
 *     new FbFixed(new RsText("oops, some big problem"))
 *   )
 * );</pre>
 *
 * <p>If and when exception occurs, {@link org.takes.facets.fallback.TkFallback}
 * will catch it and create an instance of
 * {@link org.takes.facets.fallback.RqFallback}. This object will
 * be sent to the encapsulated instance of
 * {@link org.takes.facets.fallback.Fallback}. It is recommended to use
 * {@link org.takes.facets.fallback.FbChain} to dispatch a request
 * through a series of fallbacks. The first of them who will return
 * some response will stop the chain.
 *
 * @since 0.1
 */
package org.takes.facets.fallback;
