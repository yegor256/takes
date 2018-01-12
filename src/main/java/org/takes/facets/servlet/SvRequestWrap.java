/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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
package org.takes.facets.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.takes.Request;

/**
 * Connects ServletRequest and Framework Request.
 *
 * @author Izbassar Tolegen (t.izbassar@gmail.com)
 * @version $Id.$
 * @since 2.0
 */
final class SvRequestWrap implements Request {

    /**
     * ServletRequest to be connected.
     */
    private final HttpServletRequest request;

    /**
     * Ctor.
     * @param request ServletRequest.
     */
    SvRequestWrap(final HttpServletRequest request) {
        this.request = request;
    }

    @Override
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public Iterable<String> head() throws IOException {
        final Enumeration<String> headers = this.request.getHeaderNames();
        final List<String> result = new LinkedList<>();
        while (headers.hasMoreElements()) {
            final String name = headers.nextElement();
            final StringBuilder header = new StringBuilder(name).append(": ");
            final Enumeration<String> values = this.request.getHeaders(name);
            while (values.hasMoreElements()) {
                header.append(values.nextElement()).append(' ');
            }
            result.add(header.toString().trim());
        }
        return result;
    }

    @Override
    public InputStream body() throws IOException {
        return this.request.getInputStream();
    }
}
