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
