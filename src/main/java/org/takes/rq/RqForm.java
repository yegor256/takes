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
package org.takes.rq;

import java.io.IOException;
import org.takes.Request;

/**
 * Request decorator that decodes FORM data from
 * {@code application/x-www-form-urlencoded} format (RFC 1738).
 *
 * <p>For {@code multipart/form-data} format use
 * {@link org.takes.rq.multipart.RqMtBase}.
 *
 * <p>It is highly recommended to use {@link org.takes.rq.RqGreedy}
 * decorator before passing request to this class.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @see <a href="http://www.w3.org/TR/html401/interact/forms.html">
 *     Forms in HTML</a>
 * @see org.takes.rq.RqGreedy
 * @since 0.9
 */
public interface RqForm extends Request {

    /**
     * Get single parameter.
     * @param name Parameter name
     * @return List of values (can be empty)
     * @throws IOException if something fails reading parameters
     */
    Iterable<String> param(CharSequence name) throws IOException;

    /**
     * Get all parameter names.
     * @return All names
     * @throws IOException if something fails reading parameters
     */
    Iterable<String> names() throws IOException;

}
