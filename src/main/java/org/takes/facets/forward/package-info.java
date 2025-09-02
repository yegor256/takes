/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * HTTP forwarding and redirection utilities.
 *
 * <p>Classes in this package provide functionality for automating HTTP forwarding
 * operations through 30x status code responses. When a web form is submitted via
 * POST request, returning a 303 (See Other) response instructs the browser to
 * navigate to a different page immediately. This technique prevents duplicate
 * form submissions when users click the browser's refresh button. This is how to implement it
 * in your "take":
 *
 * <pre> public class TkSaveFile implements Take {
 *   &#64;Override
 *   public Response act(final Request req) {
 *     final InputStream content =
 *       new RqMtBase(req).part("file").body();
 *     // save content to whenever you want
 *     return new RsForward(new RqHref(req).href());
 *   }
 * }</pre>
 *
 * <p>When the file is saved, this "take" will return an instance of
 * {@link org.takes.facets.forward.RsForward}, which will contain the URI
 * to be placed into {@code Location} header. The browser will use
 * this URI as a destination point of the next page to render.
 *
 * <p>Sometimes it is convenient to throw an exception instead of returning
 * a response, especially in input checking situations, for example:
 *
 * <pre> public class TkLoadFile implements Take {
 *   &#64;Override
 *   public Response act(final Request req) {
 *     final Iterable&lt;String&gt; param =
 *       new RqHref(req).href().param("name");
 *     if (!param.iterator().hasNext()) {
 *       throw RsForward(
 *         new RsFlash("query param NAME is mandatory"),
 *         HttpURLConnection.HTTP_SEE_OTHER,
 *         "/files"
 *       );
 *     }
 *     // continue normal operations
 *   }
 * }</pre>
 *
 * <p>This example will work only if you wrap your entire "take" into
 * {@link org.takes.facets.forward.TkForward} decorator:
 *
 * <pre>Take take = new TkForward(take);</pre>
 *
 * <p>This {@link org.takes.facets.forward.TkForward} decorator will catch
 * all exceptions of type {@link org.takes.facets.forward.RsForward} and
 * convert them to responses.
 *
 * @since 0.1
 */
package org.takes.facets.forward;
