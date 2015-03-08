/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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

import java.io.IOException;

/**
 * Takes.
 *
 * <p>It is a key interface in the entire framework and you should understand
 * its role. It accepts HTTP requests and dispatches to the right takes.
 * The only source of information about where such requests should
 * be dispatched an implementation of this interface can get from
 * the HTTP request itself.
 *
 * <p>There are a few classes that implement this interface and you
 * can create your own. But the best way is to start with
 * {@link org.takes.facets.fork.TsFork}, for example:
 *
 * <pre> new FtBasic(
 *   new TsFork(new FkRegex("/", "hello, world!")), 8080
 *  ).start(Exit.NEVER);
 * }</pre>
 *
 * <p>This code will start an HTTP server on port 8080 and will forward
 * all HTTP requests to the instance of class
 * {@link org.takes.facets.fork.TsFork}.
 * That object will try to find the best suitable "fork" amongst all
 * encapsulated objects. There is only one in the example above &mdash;
 * an instance of {@link org.takes.facets.fork.FkRegex}.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 * @see org.takes.Take
 */
public interface Takes {

    /**
     * Dispatch this request.
     * @param request The request to dispatch
     * @return Take to process
     * @throws IOException If fails
     */
    Take route(Request request) throws IOException;

}
