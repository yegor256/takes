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
package org.takes.facets.fork;

import java.io.IOException;
import org.junit.Test;
import org.mockito.Mockito;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rq.RqMethod;

/**
 * Test case for {@link TkMethods}.
 * @author Aleksey Popov (alopen@yandex.ru)
 * @version $Id$
 * @since 0.17
 */
public final class TkMethodsTest {
    /**
     * TkMethods can call act on method that is passes to it.
     * @throws Exception if any error occurs
     */
    @Test
    public void callsActOnProperMethods() throws Exception {
        final Take take = Mockito.mock(Take.class);
        final Request req = new RqFake(RqMethod.GET);
        new TkMethods(take, RqMethod.GET).act(req);
        Mockito.verify(take).act(req);
    }

    /**
     * TkMethods can throw HttpExcection when acting on unproper method.
     * @throws IOException if any I/O error occurs.
     */
    @Test(expected = HttpException.class)
    public void throwsExceptionOnActinOnUnproperMethod() throws
        IOException {
        new TkMethods(Mockito.mock(Take.class), RqMethod.POST).act(
            new RqFake(RqMethod.GET)
        );
    }
}
