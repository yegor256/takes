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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test case for {@link VerboseIterable}.
 * @author Zarko Celebic (zarko.celebic@gmail.com)
 * @version $Id$
 */
public final class VerboseIterableTest {

    // @checkstyle JavadocVariableCheck (6 lines)
    private static String one = "1";
    private static String two = "2";
    private static String three = "3";
    private static String errormessage = "Error message";

    @Rule
    // @checkstyle VisibilityModifierCheck (1 line)
    public transient ExpectedException expected = ExpectedException.none();

    /**
     * VerboseIterable can return iterator.
     * @throws Exception If some problem inside
     */
    @Test
    public void returnIterator() throws Exception {
        final List<String> testList = Arrays.asList(one, two, three);
        final VerboseIterable<String> vIterable = new VerboseIterable<String>(
                testList, errormessage
        );
        final Iterator<String> iter = vIterable.iterator();
        MatcherAssert.assertThat(iter.hasNext(), Matchers.equalTo(true));
        MatcherAssert.assertThat(iter.next(), Matchers.equalTo(one));
        MatcherAssert.assertThat(iter.hasNext(), Matchers.equalTo(true));
        MatcherAssert.assertThat(iter.next(), Matchers.equalTo(two));
        MatcherAssert.assertThat(iter.hasNext(), Matchers.equalTo(true));
        MatcherAssert.assertThat(iter.next(), Matchers.equalTo(three));
        MatcherAssert.assertThat(iter.hasNext(), Matchers.equalTo(false));
        this.expected.expect(NoSuchElementException.class);
        this.expected.expectMessage(errormessage);
        iter.next();
    }
}
