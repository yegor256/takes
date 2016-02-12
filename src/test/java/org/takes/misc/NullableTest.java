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
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link Opt.Nullable}.
 * @author I. Sokolov (happy.neko@gmail.com)
 * @version $Id$
 * @since 0.32
 */
public final class NullableTest {
    /**
     * Item to hold in {@link Opt.Nullable} container.
     */
    private transient Object item;

    /**
     * Make item instance.
     * @throws Exception If something goes wrong
     */
    @Before
    public void setUp() throws Exception {
        this.item = new Object();
    }

    /**
     * Has should return true for non-empty container.
     * @throws Exception If something goes wrong
     */
    @Test
    public void hasNonNullItemShouldReturnTrue() throws Exception {
        MatcherAssert.assertThat(
            new Opt.Nullable<>(this.item).has(),
            Matchers.is(true)
        );
    }

    /**
     * Has should return false for empty container.
     * @throws Exception If something goes wrong
     */
    @Test
    public void hasNullItemShouldReturnFalse() throws Exception {
        MatcherAssert.assertThat(
            new Opt.Nullable<>(null).has(),
            Matchers.is(false)
        );
    }

    /**
     * Get should return item for non-empty container.
     * @throws Exception If something goes wrong
     */
    @Test
    public void getNonNullItemShouldReturnIt() throws Exception {
        MatcherAssert.assertThat(
            new Opt.Nullable<>(this.item).get(),
            Matchers.sameInstance(this.item)
        );
    }

    /**
     * Get should throw NoSuchElementException for empty container.
     * @throws Exception If something goes wrong
     */
    @Test(expected = NoSuchElementException.class)
    public void failsOnGetNullItem() throws Exception {
        new Opt.Nullable<>(null).get();
    }
}
