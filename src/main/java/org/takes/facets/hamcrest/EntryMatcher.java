/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Yegor Bugayenko
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
package org.takes.facets.hamcrest;

import java.util.Map;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher for {@link Map.Entry}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Dragan Bozanovic (bozanovicdr@gmail.com)
 * @version $Id$
 * @since 0.27
 * @param <K> Key
 * @param <V> Value
 */
public final class EntryMatcher<K, V> extends TypeSafeMatcher<Map.Entry<K, V>> {

    /**
     * Key matcher.
     */
    private final transient Matcher<K> keym;

    /**
     * Value matcher.
     */
    private final transient Matcher<V> valuem;

    /**
     * Ctor.
     * @param key Key
     * @param value Value
     */
    public EntryMatcher(final K key, final V value) {
        super();
        this.keym = Matchers.equalTo(key);
        this.valuem = Matchers.equalTo(value);
    }

    @Override
    public boolean matchesSafely(final Map.Entry<K, V> entry) {
        return this.keym.matches(entry.getKey())
            && this.valuem.matches(entry.getValue());
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("entry containing [")
            .appendDescriptionOf(this.keym)
            .appendText("->")
            .appendDescriptionOf(this.valuem)
            .appendText("]");
    }
}
