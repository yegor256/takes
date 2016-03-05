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

package org.takes.misc;

import java.util.NoSuchElementException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for Optional.
 * @author I. Sokolov (happy.neko@gmail.com)
 * @version $Id$
 * @since 0.32
 */
public final class OptionalTest {
    /**
     * Optional can return true for non-empty container.
     * @throws Exception If something goes wrong
     */
    @Test
    public void returnsTrueForNonEmptyContainer() throws Exception {
        MatcherAssert.assertThat(
            new Optional<>(new Object()).has(),
            Matchers.is(true)
        );
    }

    /**
     * Optional can return false for empty container.
     * @throws Exception If something goes wrong
     */
    @Test
    public void returnsFalseForEmptyContainer() throws Exception {
        MatcherAssert.assertThat(
            new Optional<>(null).has(),
            Matchers.is(false)
        );
    }

    /**
     * Optional can return item for non-empty container.
     * @throws Exception If something goes wrong
     */
    @Test
    public void returnsItemForNonEmptyContainer() throws Exception {
        final Object item = new Object();
        MatcherAssert.assertThat(
            new Optional<>(item).get(),
            Matchers.sameInstance(item)
        );
    }

    /**
     * Optional can throw NoSuchElementException for empty
     * container.
     * @throws Exception If something goes wrong
     */
    @Test(expected = NoSuchElementException.class)
    public void throwsNoSuchElementExceptionForEmptyContainer()
        throws Exception {
        new Optional<>(null).get();
    }
}
