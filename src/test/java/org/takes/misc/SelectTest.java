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
package org.takes.misc;

import com.google.common.base.Joiner;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Select}.
 *
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @since 0.13.8
 */
public final class SelectTest {

    /**
     * Select can select with condition.
     */
    @Test
    public void selectsWithCondition() {
        MatcherAssert.assertThat(
            Joiner.on(" ").join(
                new Select<String>(
                    Arrays.asList("one", "two", "three1", "four1"),
                    new Condition<String>() {
                        @Override
                        public boolean fits(final String element) {
                            return element.endsWith("1");
                        }
                    }
                )
            ),
            Matchers.equalTo("three1 four1")
        );
    }

    /**
     * Select can select with not condition.
     */
    @Test
    public void selectsWithNotCondition() {
        MatcherAssert.assertThat(
            Joiner.on("+").join(
                new Select<String>(
                    Arrays.asList("at4", "at5", "at61"),
                    new Condition.Not<String>(
                        new Condition<String>() {
                            @Override
                            public boolean fits(final String element) {
                                return element.endsWith("61");
                            }
                        }
                    )
                )
            ),
            Matchers.equalTo("at4+at5")
        );
    }

    /**
     * Select will select all with true condition.
     */
    @Test
    public void selectsAllWithTrueCondition() {
        MatcherAssert.assertThat(
            Joiner.on(" ").join(
                new Select<String>(
                    Arrays.asList("1", "2", "33", "4"),
                    new Condition.True<String>()
                )
            ),
            Matchers.equalTo("1 2 33 4")
        );
    }

    /**
     * Select can select according to regex.
     */
    @Test
    public void selectsWithRegex() {
        MatcherAssert.assertThat(
            Joiner.on(" ").join(
                new Select<String>(
                    Arrays.asList("true", "Yes", "no", "True"),
                    new Condition.Regexp("[tT]rue|[yY]es")
                )
            ),
            Matchers.equalTo("true Yes True")
        );
    }

    /**
     * Select can select from starting position.
     */
    @Test
    public void selectsFromStartingPosition() {
        MatcherAssert.assertThat(
            Joiner.on(".").join(
                new Select<Integer>(
                    // @checkstyle MagicNumberCheck (2 lines)
                    Arrays.asList(2, 4, 6, 8, 10, 12),
                    new Condition.Range<Integer>(3)
                )
            ),
            Matchers.equalTo("8.10.12")
        );
    }

    /**
     * Select can select within given range.
     */
    @Test
    public void selectsWithinGivenRange() {
        MatcherAssert.assertThat(
            Joiner.on(",").join(
                new Select<Integer>(
                    // @checkstyle MagicNumberCheck (2 lines)
                    Arrays.asList(3, 5, 7, 9, 11, 13, 15),
                    new Condition.Range<Integer>(2, 4)
                )
            ),
            Matchers.equalTo("7,9,11")
        );
    }
}
