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
 * Forward.
 *
 * <p>Classes in this package help you to automate forwarding operations,
 * through 30x HTTP responses. For example, when a web form is submitted
 * through POST request, it is a good practice to return a 303 response
 * which will instruct the browser to change the web page immediately. This
 * technique is used mostly in order to prevent duplicate form submissions
 * by hitting "Refresh" button in the browser. This is how you implement it
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
