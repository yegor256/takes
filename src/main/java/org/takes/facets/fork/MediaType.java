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
import com.jcabi.aspects.Cacheable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
@ToString
@EqualsAndHashCode(of = { "text" })
final class MediaType implements Comparable<MediaType> {

    /**
     * Text.
     */
    private final transient String text;

    /**
     * Ctor.
     * @param text Text to parse
     */
    MediaType(final String text) {
        this.text = text;
    }

    @Override
    public int compareTo(final MediaType type) {
        int cmp = this.getPriority().compareTo(type.getPriority());
        if (cmp == 0) {
            cmp = this.getHigh().compareTo(type.getHigh());
            if (cmp == 0) {
                cmp = this.getLow().compareTo(type.getLow());
            }
        }
        return cmp;
    }

    /**
     * Matches.
     * @param type Another type
     * @return TRUE if matches
     * @checkstyle BooleanExpressionComplexityCheck (10 lines)
     */
    public boolean matches(final MediaType type) {
        final String star = "*";
        return (this.getHigh().equals(star)
            || type.getHigh().equals(star)
            || this.getHigh().equals(type.getHigh()))
            && (this.getLow().equals(star)
            || type.getLow().equals(star)
            || this.getLow().equals(type.getLow()));
    }

    /**
     * Get the parts.
     * @return The parts of the media type.
     */
    @Cacheable(forever = true)
    private String[] getParts() {
        return this.text.split(";", 2);
    }
    
    /**
     * Get the priority.
     * @return The priority of the media type.
     */
    @Cacheable(forever = true)
    private Double getPriority() {
        final String[] parts = this.getParts();
        if (parts.length > 1) {
            final String num = parts[1].replaceAll("[^0-9\\.]", "");
            if (num.isEmpty()) {
                return 0.0d;
            } else {
                return Double.parseDouble(num);
            }
        } else {
            return 1.0d;
        }
    }

    /**
     * Get the high part.
     * @return The high part of the media type.
     */
    @Cacheable(forever = true)
    private String getHigh() {
        final String[] sectors = this.getSectors();
        return sectors[0];
    }

    /**
     * Get the low part.
     * @return The low part of the media type.
     */
    @Cacheable(forever = true)
    private String getLow() {
        final String[] sectors = this.getSectors();
        if (sectors.length > 1) {
            return sectors[1].trim();
        } else {
            return "";
        }
    }

    /**
     * Get the sectors.
     * @return String array with the sectors of the media type.
     */
    @Cacheable(forever = true)
    private String[] getSectors() {
        return getParts()[0]
                .toLowerCase(Locale.ENGLISH).split("/", 2);
    }
    
}
