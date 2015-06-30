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
package org.takes.facets.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.misc.Opt;
import org.takes.rq.RqFake;

/**
 * Test of {@link PsAll}.
 * @author Lautaro Cozzani (lautarobock@gmail.com)
 * @version $Id$
 */
public final class PsAllTest {

    /**
     * Fail if there is not pass.
     * @throws IOException if exception
     */
    @Test
    public void testEmpty() throws IOException {
        MatcherAssert.assertThat(
            new PsAll(new ArrayList<Pass>(0), 0).enter(new RqFake()).has(),
            Matchers.is(false)
        );
    }

    /**
     * Success with only one successfull pass.
     * @throws IOException if exception
     */
    @Test
    public void testOneSuccessfull() throws IOException {
        final List<Pass> list = new ArrayList<Pass>(1);
        list.add(new PsFake(true));
        final Opt<Identity> identity = new PsAll(list, 0).enter(new RqFake());
        MatcherAssert.assertThat(
            identity.has(),
            Matchers.is(true)
        );
    }

    /**
     * Fail with only one failure test.
     * @throws IOException if exception
     */
    @Test
    public void testOneFail() throws IOException {
        final List<Pass> list = new ArrayList<Pass>(1);
        list.add(new PsFake(false));
        final Opt<Identity> identity = new PsAll(list, 0).enter(new RqFake());
        MatcherAssert.assertThat(
            identity.has(),
            Matchers.is(false)
        );
    }

    /**
     * Gets idx identity if all pass success.
     * @throws IOException if exception
     */
    @Test
    public void testSuccessfullIdx() throws IOException {
        final List<Pass> list = new ArrayList<Pass>(2);
        list.add(new PsFake(true));
        list.add(new PsFake(true));
        final Opt<Identity> identity = new PsAll(list, 1).enter(new RqFake());
        MatcherAssert.assertThat(
            identity.has(),
            Matchers.is(true)
        );
    }

    /**
     * Fail if one of Pass fails.
     * @throws IOException if exception
     */
    @Test
    public void testFail() throws IOException {
        final List<Pass> list = new ArrayList<Pass>(2);
        list.add(new PsFake(true));
        list.add(new PsFake(false));
        final Opt<Identity> identity = new PsAll(list, 1).enter(new RqFake());
        MatcherAssert.assertThat(
            identity.has(),
            Matchers.is(false)
        );
    }

    /**
     * Fail if idx of identity is over pass count.
     * @throws IOException if exception
     */
    @Test
    public void testFailOverflow() throws IOException {
        final List<Pass> list = new ArrayList<Pass>(2);
        list.add(new PsFake(true));
        list.add(new PsFake(true));
        final Opt<Identity> identity = new PsAll(list, 2).enter(new RqFake());
        MatcherAssert.assertThat(
            identity.has(),
            Matchers.is(false)
        );
    }
}
