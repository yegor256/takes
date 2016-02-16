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

import com.jcabi.aspects.Cacheable;
import java.util.Locale;
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
@EqualsAndHashCode(of = "text")
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
        int cmp = this.priority().compareTo(type.priority());
        if (cmp == 0) {
            cmp = this.highPart().compareTo(type.highPart());
            if (cmp == 0) {
                cmp = this.lowPart().compareTo(type.lowPart());
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
        return (this.highPart().equals(star)
            || type.highPart().equals(star)
            || this.highPart().equals(type.highPart()))
            && (this.lowPart().equals(star)
            || type.lowPart().equals(star)
            || this.lowPart().equals(type.lowPart()));
    }

    /**
     * Splits the text parts.
     * @return The parts of the media type.
     */
    @Cacheable(forever = true)
    private String[] split() {
        return this.text.split(";", 2);
    }

    /**
     * Returns the media type priority.
     * @return The priority of the media type.
     */
    @Cacheable(forever = true)
    private Double priority() {
        final String[] parts = this.split();
        Double priority = 1.0d;
        if (parts.length > 1) {
            final String num = parts[1].replaceAll("[^0-9\\.]", "");
            if (num.isEmpty()) {
                priority = 0.0d;
            } else {
                priority = Double.parseDouble(num);
            }
        }
        return priority;
    }

    /**
     * Returns the high part of the media type.
     * @return The high part of the media type.
     */
    @Cacheable(forever = true)
    private String highPart() {
        final String[] sectors = this.sectors();
        return sectors[0];
    }

    /**
     * Returns the low part of the media type.
     * @return The low part of the media type.
     */
    @Cacheable(forever = true)
    private String lowPart() {
        String sector = "";
        final String[] sectors = this.sectors();
        if (sectors.length > 1) {
            sector = sectors[1].trim();
        }
        return sector;
    }

    /**
     * Returns the media type sectors.
     * @return String array with the sectors of the media type.
     */
    @Cacheable(forever = true)
    private String[] sectors() {
        return this.split()[0].toLowerCase(Locale.ENGLISH).split("/", 2);
    }

}
