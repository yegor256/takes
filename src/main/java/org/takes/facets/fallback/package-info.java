/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Exception handling and fallback mechanisms.
 *
 * <p>Exception handling in the framework is very simple and very
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
