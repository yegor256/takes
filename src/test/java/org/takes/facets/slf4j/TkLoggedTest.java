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

package org.takes.facets.slf4j;

import java.io.IOException;
import org.junit.Test;
import org.mockito.Mockito;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.tk.TkText;

/**
 * Test case for {@link TkLogged}.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.11.2
 */
public final class TkLoggedTest {
    /**
     * TkLogged can log message.
     * @throws IOException If some problem inside
     */
    @Test
    public void logsMessage() throws IOException {
        final Target target = Mockito.mock(Target.class);
        new TkLogged(new TkText("test"), target).act(new RqFake());
        Mockito.verify(target, Mockito.times(1)).log(
                Mockito.eq("[{}] #act() return [{}] in [{}] ms"),
                Mockito.isA(Take.class),
                Mockito.isA(Response.class),
                Mockito.anyLong()
        );
    }
}
