/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.util.List;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.Text;
import org.cactoos.list.ListOf;
import org.cactoos.text.Lowered;
import org.cactoos.text.Split;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;
import org.cactoos.text.UncheckedText;

/**
 * Media type.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @see org.takes.facets.fork.FkTypes
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
final class MediaType implements Comparable<MediaType> {

    /**
     * Pattern matching non-digit symbols.
     */
    private static final Pattern NON_DIGITS = Pattern.compile("[^0-9\\.]");

    /**
     * Priority.
     */
    private final Double prio;

    /**
     * High part.
     */
    private final String high;

    /**
     * Low part.
     */
    private final String low;

    /**
     * Ctor.
     * @param text Text to parse
     */
    MediaType(final String text) {
        this.prio = MediaType.priority(text);
        this.high = MediaType.highPart(text);
        this.low = MediaType.lowPart(text);
    }

    @Override
    public int compareTo(final MediaType type) {
        int cmp = this.prio.compareTo(type.prio);
        if (cmp == 0) {
            cmp = this.high.compareTo(type.high);
            if (cmp == 0) {
                cmp = this.low.compareTo(type.low);
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
        return (this.high.equals(star)
            || type.high.equals(star)
            || this.high.equals(type.high))
            && (this.low.equals(star)
            || type.low.equals(star)
            || this.low.equals(type.low));
    }

    /**
     * Splits the text parts.
     * @param text The text to be split.
     * @return Two first parts of the media type.
     */
    private static List<Text> split(final String text) {
        return new ListOf<>(
            new Split(
                new TextOf(text),
                ";",
                2
            )
        );
    }

    /**
     * Returns the media type priority.
     * @param text The media type text.
     * @return The priority of the media type.
     */
    private static Double priority(final String text) {
        final List<Text> parts = MediaType.split(text);
        final Double priority;
        if (parts.size() > 1) {
            final String num = MediaType.NON_DIGITS.matcher(
                new UncheckedText(
                    parts.get(1)
                ).asString()
            ).replaceAll("");
            if (num.isEmpty()) {
                priority = 0.0d;
            } else {
                priority = Double.parseDouble(num);
            }
        } else {
            priority = 1.0d;
        }
        return priority;
    }

    /**
     * Returns the high part of the media type.
     * @param text The media type text.
     * @return The high part of the media type.
     */
    private static String highPart(final String text) {
        return new UncheckedText(
            MediaType.sectors(text).get(0)
        ).asString();
    }

    /**
     * Returns the low part of the media type.
     * @param text The media type text.
     * @return The low part of the media type.
     */
    private static String lowPart(final String text) {
        final List<Text> sectors = MediaType.sectors(text);
        final Text sector;
        if (sectors.size() > 1) {
            sector = new Trimmed(sectors.get(1));
        } else {
            sector = new TextOf("");
        }
        return new UncheckedText(sector).asString();
    }

    /**
     * Returns the media type sectors.
     * @param text The media type text.
     * @return Sectors of the media type.
     */
    private static List<Text> sectors(final String text) {
        return new ListOf<>(
            new Split(
                new UncheckedText(new Lowered(MediaType.split(text).get(0))),
                "/",
                2
            )
        );
    }

}
