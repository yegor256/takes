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

package org.takes.facets.hamcrest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Body Matcher.
 *
 * <p>This "matcher" tests given item body</p>
 *
 * @author Tolegen Izbassar (t.izbassar@gmail.com)
 * @version $Id$
 * @param <T> Item type. Should be able to return own item
 * @since 2.0
 *
 * @todo #485:30min Right now we can only check that InputStream
 *  have the same content as other InputStream. This is very
 *  limited usage. Task is to introduce `HmTextBody` that will
 *  make available to us to use useful string matchers from
 *  hamcrest. The usage will be like that:
 *  ```
 *  MatcherAssert.assertThat(
 *      response,
 *      new HmRsTextBody<>(Matchers.startsWith("<html>"))
 *  );
 *  ```
 *  The default constructor should use `Matcher.containsString`
 *  as default matcher, which is used for matching string to body.
 *  Current implementation of `AbstractHmBody` should be converted
 *  to `HmBytesBody` that will check equality of bytes. We can think
 *  of improving that class lately.
 *
 * @todo #485:30min Right now the describeTo doesn't properly
 *  show the reason behind mismatch. It should show expected
 *  bytes and actual bytes for better clarification for
 *  end user. Also describeMismatchSafely should be implemented.
 */
abstract class AbstractHmBody<T> extends TypeSafeMatcher<T> {

    /**
     * Body.
     */
    private final InputStream body;

    /**
     * Ctor.
     * @param value Value to test against.
     */
    protected AbstractHmBody(final InputStream value) {
        super();
        this.body = value;
    }

    @Override
    public final void describeTo(final Description description) {
        description.appendText("item: ")
            .appendValue(this.body);
    }

    @Override
    protected final boolean matchesSafely(final T item) {
        boolean result = true;
        try (
            final InputStream val = new BufferedInputStream(this.itemBody(item))
        ) {
            int left = this.body.read();
            while (left != -1) {
                final int right = val.read();
                if (left != right) {
                    result = false;
                    break;
                }
                left = this.body.read();
            }
            final int right = val.read();
            if (result) {
                result = right == -1;
            }
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
        return result;
    }

    /**
     * Item's body.
     * @param item Item to retrieve body from
     * @return InputStream of body
     * @throws IOException If some problem inside
     */
    protected abstract InputStream itemBody(final T item) throws IOException;
}

