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

/**
 * Action for {@link Transform} to perform actual transformation.
 *
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @param <T> Type of item
 * @param <K> Type of key
 * @since 0.15.2
 */
public interface TransformAction<T, K> {
    /**
     * The transform action of the element of type T to K.
     * @param element Element of the iterable
     * @return Transformed element
     */
    K transform(T element);

    /**
     * Trimming action used with {@link Transform}.
     */
    final class Trim implements TransformAction<String, String> {
        @Override
        public String transform(final String element) {
            return element.trim();
        }
    }

    /**
     * Convert CharSequence into String.
     */
    final class ToString implements TransformAction<CharSequence, String> {
        @Override
        public String transform(final CharSequence element) {
            return element.toString();
        }
    }
}
