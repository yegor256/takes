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
package org.takes.facets.fork;

import java.util.Locale;
import lombok.EqualsAndHashCode;

/**
 * Media type.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.6
 * @see org.takes.facets.fork.FkTypes
 */
@EqualsAndHashCode(of = { "high", "low" })
final class MediaType implements Comparable<MediaType> {

    /**
     * Priority.
     */
    private final transient Double priority;

    /**
     * High part.
     */
    private final transient String high;

    /**
     * Low part.
     */
    private final transient String low;

    /**
     * Ctor.
     * @param text Text to parse
     */
    MediaType(final String text) {
        final String[] parts = text.split(";", 2);
        if (parts.length > 1) {
            this.priority = Double.parseDouble(parts[1].trim().substring(2));
        } else {
            this.priority = 1.0d;
        }
        final String[] sectors = parts[0].trim()
            .toLowerCase(Locale.ENGLISH).split("/");
        this.high = sectors[0];
        this.low = sectors[1];
    }

    /**
     * Ctor.
     * @param prio Priority
     * @param left Left part
     * @param right Right part
     */
    MediaType(final double prio, final String left,
        final String right) {
        this.priority = prio;
        this.high = left;
        this.low = right;
    }

    @Override
    public int compareTo(final MediaType type) {
        return this.priority.compareTo(type.priority);
    }

    /**
     * Matches.
     * @param type Another type
     * @return TRUE if matches
     * @checkstyle BooleanExpressionComplexityCheck (10 lines)
     */
    public boolean matches(final MediaType type) {
        final String star = "*";
        return (this.high.equals(star)
            || type.high.equals(star)
            || this.high.equals(type.high))
            && (this.low.equals(star)
            || type.low.equals(star)
            || this.low.equals(type.low));
    }

}
