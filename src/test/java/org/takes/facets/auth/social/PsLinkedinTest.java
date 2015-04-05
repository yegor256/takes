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

package org.takes.facets.auth.social;

import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link PsLinkedin}.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.11.3
 */
public final class PsLinkedinTest {

    /**
     * PsLinkedin can login.
     * @throws IOException If some problem inside
     * @todo #12:30min/DEV This class not implemented yet, but has to be.
     *  Please implement it. Mocked OAuth server is one of the possible
     *  solutions. In this case, we could use some library like WireMock
     *  (see http://www.wiremock.org), which allows make stub for web
     *  service. We'll need to make some changes in PsLinkedin,
     *  that allow to change API url, and use local stubbed http
     *  server during unit test.
     */
    @Ignore
    @Test
    public void logins() throws IOException {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
