/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Flash message implementation for temporary cross-request communication.
 *
 * <p>Flash messages provide a technique for sending short texts from one web
 * page to another without requiring server-side persistence. The mechanism
 * works as follows: a web page performs an operation (such as posting to a
 * discussion thread) and returns an HTTP response with redirection status 303.
 * The response contains a Set-Cookie header with a flash message like
 * "thanks for the post".
 *
 * <p>The browser preserves the cookie and redirects to the target page. When
 * the browser makes the new HTTP request to render the discussion thread content,
 * it includes the flash message in the Cookie header. The server extracts this
 * message, adds it to the HTML page to inform the user about the completed action,
 * and returns an HTTP response containing a Set-Cookie header with an expired
 * value to remove the cookie.
 *
 * <p>This ensures flash messages are displayed only once, as they are deleted
 * after first use. The server controls deletion by returning a Set-Cookie
 * header with an expired date.
 *
 * <p>Classes in this package help to automate this mechanism. First,
 * you add flash messages to your responses using
 * {@link org.takes.facets.flash.RsFlash}:
 *
 * <pre>public final class TkDiscussion implements Take {
 *   &#64;Override
 *   public Response act(final Request req) {
 *     // save the post to the database
 *     return new RsFlash(
 *       new RsForward(),
 *       "thanks for the post"
 *     );
 *   }
 * }</pre>
 *
 * <p>This {@link org.takes.facets.flash.RsFlash} decorator will add the
 * required "Set-Cookie" header to the response. This is all it is doing.
 * The response is added to the cookie in URL-encoded format, together
 * with the logging level. Flash messages could be of different severity,
 * we're using Java logging levels for that, for example:
 *
 * <pre>public final class TkDiscussion implements Take {
 *   &#64;Override
 *   public Response act(final Request req) {
 *     if (failed) {
 *       throw new RsFlash(
 *         new RsForward(),
 *         "can't save your post, sorry",
 *         java.util.logging.Level.SEVERE
 *       );
 *     }
 *   }
 * }</pre>
 *
 * <p>This is how the HTTP response will look like (simplified):
 *
 * <pre> HTTP/1.1 303 See Other
 * Set-Cookie: RsFlash=can%27t%20save%20your%20post%2C%20sorry/SEVERE</pre>
 *
 * <p>Here, the name of the cookie is {@code RsFlash}. You can change this
 * default name using a constructor of {@link org.takes.facets.flash.RsFlash},
 * but it's not recommended. It's better to use the default name.
 *
 * <p>The next step is to understand that cookie on its way back,
 * from the browser to the server. This is what a browser will send back:
 *
 * <pre> GET / HTTP/1.1
 * Host: www.example.com
 * Cookie: RsFlash=can%27t%20save%20your%20post%2C%20sorry/SEVERE</pre>
 *
 * <p>There is a class {@link org.takes.facets.flash.TkFlash}, that
 * decorates your existing "take" and adds "Set-Cookie" with an empty
 * value to the response. That's all it's doing. All you need to do
 * is to decorate your existing "take", for example:
 *
 * <pre> new FtBasic(
 *   new TkFlash(TkFork(new FkRegex("/", "hello, world!"))), 8080
 *  ).start(Exit.NEVER);
 * }</pre>
 *
 * <p>The last step is to fetch that cookie from the request and add
 * to the HTML page. You can use
 * {@link org.takes.facets.cookies.RqCookies} for that
 * (it's a pseudo-code, don't build HTML like this!):
 *
 * <pre>public final class TkDiscussion implements Take {
 *   &#64;Override
 *   public Response act(final Request req) {
 *     String html = "this is our discussion thread...";
 *     final Iterator&lt;String&gt; cookies =
 *       new RqCookies(req).cookie("RsFlash").iterator();
 *       if (cookies.hasNext()) {
 *         html = cookies.next() + html;
 *       }
 *       return new RsHTML(html);
 *     }
 *   }
 * }</pre>
 *
 * <p>If you're using Xembly to build XML output, you can use
 * {@link org.takes.facets.flash.XeFlash} for fetching flash messages
 * from cookies and adding them to XML:
 *
 * <pre>public final class TkDiscussion implements Take {
 *   private final Request req;
 *   &#64;Override
 *   public Response act(final Request req) {
 *     return new RsXembly(
 *       new XeAppend(
 *         "page",
 *         new XeFlash(req),
 *         // your other Xembly sources
 *       )
 *     );
 *   }
 * }</pre>
 *
 * <p>Don't forget that the cookie you receive is not just a flash message,
 * but also other parameters, URL-encoded and separated by "slash".</p>
 *
 * @since 0.1
 */
package org.takes.facets.flash;
