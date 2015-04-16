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

import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Concat}.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.32.1
 */
public final class ConcatTest {

    /**
     * Basic concat unit test.
     */
    @Test
    public void concat() {
        final List<String> alist = new ArrayList<String>(2);
        String astr1 = "a1";
        String astr2 = "a2";
        alist.add(astr1);
        alist.add(astr2);
        final List<String> blist = new ArrayList<String>(2);
        String bstr1 = "b1";
        String bstr2 = "b2";
        blist.add(bstr1);
        blist.add(bstr2);
        MatcherAssert.assertThat(
                (Iterable<String>) new Concat<String>(alist, blist),
                Matchers.hasItems(astr1, astr2, bstr1, bstr2)
        );
    }

    /**
     * Concat test with empty inputs.
     */
    @Test
    public void concatWithEmpty() {
        final List<String> alist = new ArrayList<String>(2);
        String astr1 = "an1";
        String astr2 = "an2";
        alist.add(astr1);
        alist.add(astr2);
        final List<String> blist = new ArrayList<String>(0);

        MatcherAssert.assertThat(
                (Iterable<String>) new Concat<String>(alist, blist),
                Matchers.hasItems(astr1, astr2)
        );
        MatcherAssert.assertThat(
                (Iterable<String>) new Concat<String>(alist, blist),
                Matchers.not(Matchers.hasItems(""))
        );
        // ensure concat empty lists will be empty
        MatcherAssert.assertThat(
                (Iterable<String>) new Concat<String>(blist, blist),
                Matchers.emptyIterable()
        );
    }

    /**
     * Concat test with condition.
     */
    @Test
    public void concatWithCondition() {
        final List<String> alist = new ArrayList<String>(2);
        String astr1 = "at1";
        String astr2 = "at2";
        alist.add(astr1);
        alist.add(astr2);
        final List<String> blist = new ArrayList<String>(2);
        String bstr1 = "bt1";
        String bstr2 = "bt2";
        blist.add(bstr1);
        blist.add(bstr2);

        Iterable<String> result = new Concat<String>(
                alist, 
                blist,
                new Concat.Condition<String>() {
                    @Override
                    public boolean add(final String element) {
                        return element.endsWith("1");
                    }
                }
                );

        MatcherAssert.assertThat(result, Matchers.hasItems(astr1, bstr1));
        MatcherAssert.assertThat(
                result,
                Matchers.not(Matchers.hasItems(astr2, bstr2))
        );
    }

}
