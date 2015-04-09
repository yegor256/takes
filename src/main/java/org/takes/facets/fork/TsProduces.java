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
package org.takes.facets.fork;

import java.io.IOException;

import org.takes.Request;
import org.takes.Take;
import org.takes.Takes;
import org.takes.ts.TsWrap;

/**
 * Takes that understands Accept header.
 *
 * <p>This is the implementation of {@link org.takes.Takes} that
 * routes the requests to another takes, using a collection of forks
 * to pick the right one. The best example is a routing by regular
 * expression, for example:
 *
 * <pre> Takes takes = new TsProduces(takes, "application/json");i</pre>
 *
 * <p>Here, {@link org.takes.facets.fork.TsFork} will try to call these
 * "forks" one by one, asking whether they accept the request. The first
 * one that reacts will get control. Each "fork" is an implementation
 * of {@link org.takes.facets.fork.Fork.AtTake}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.4
 * @see org.takes.facets.fork.FkMethods
 * @see org.takes.facets.fork.FkRegex
 * @see org.takes.facets.fork.FkParams
 */
public class TsProduces implements Takes {
	
	private final String accept;
	private final Takes takes;
    /**
     * Ctor.
     *
     * @param takes Original takes
     * @param accept headers
     */
    public TsProduces(final Takes takes, final String acpt) {
    	this.takes = takes;
        this.accept = acpt;
    }
    
    public Take route(Request request) throws IOException {
    	final FkTypes types = new FkTypes(accept, takes.route(request));
    	return new TsFork(types).route(request);
    }

}
