/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes;

/**
 * Take.
 *
 * <p>Take is a momentary snapshot of in-server reality, visible to the
 * end user via printable {@link Response}.
 * For example, this is a simple web server
 * that returns "hello, world!" plain text web page:
 *
 * <pre> new FtBasic(
 *   new Take() {
 *     &#64;Override
 *     public Response act(final Request req) {
 *       return new RsText("hello, world!");
 *     }
 *   },
 *   8080
 *  ).start(Exit.NEVER);
 * }</pre>
 *
 * <p>There are a few classes that implement this interface, and you
 * can create your own. But the best way is to start with
 * {@link org.takes.facets.fork.TkFork}, for example:
 *
 * <pre> new FtBasic(
 *   new TkFork(new FkRegex("/", "hello, world!")), 8080
 *  ).start(Exit.NEVER);
 * }</pre>
 *
 * <p>This code will start an HTTP server on port 8080 and will forward
 * all HTTP requests to the instance of class
 * {@link org.takes.facets.fork.TkFork}.
 * That object will try to find the best suitable "fork" amongst all
 * encapsulated objects. There is only one in the example above &mdash;
 * an instance of {@link org.takes.facets.fork.FkRegex}.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @see <a href="http://www.yegor256.com/2015/03/22/takes-java-web-framework.html">Java Web App Architecture In Takes Framework</a>
 * @since 0.1
 */
public interface Take {

    /**
     * Convert request to response.
     * @param req Request to process
     * @return Response
     * @throws Exception If fails
     */
    Response act(Request req) throws Exception;

}
