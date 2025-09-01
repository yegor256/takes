/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.misc;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Interface for HTTP expiration date formatting and management.
 *
 * <p>This interface provides functionality to format expiration dates
 * for HTTP headers, particularly for cache control and cookie expiration.
 * All dates are formatted in GMT timezone according to HTTP specifications.
 * The interface includes several implementations for common expiration
 * scenarios including never expiring, already expired, and timed expiration.
 *
 * <p>All implementations must be immutable and thread-safe.
 *
 * @since 2.0
 */
public interface Expires {

    /**
     * String representation of expiration time.
     * @return Representation of expiration time.
     */
    String print();

    /**
     * Implementation that represents content that never expires.
     *
     * <p>This implementation creates an expiration date set to epoch (0L)
     * which effectively means the content never expires according to HTTP
     * caching semantics.
     *
     * @since 2.0
     */
    final class Never implements Expires {

        /**
         * Original time.
         */
        private final Expires origin;

        /**
         * Constructor.
         */
        Never() {
            this.origin = new Date(0L);
        }

        @Override
        public String print() {
            return this.origin.print();
        }
    }

    /**
     * Implementation that represents already expired content.
     *
     * <p>This implementation returns "Expires=0" which indicates that
     * the content has already expired according to RFC 7234. This is
     * useful for immediate cache invalidation.
     *
     * @since 2.0
     */
    final class Expired implements Expires {
        @Override
        public String print() {
            return "Expires=0";
        }
    }

    /**
     * Implementation that represents content expiring in one hour.
     *
     * <p>This implementation wraps another Expires instance and represents
     * content that expires one hour from the given base time. It delegates
     * to the wrapped instance for the actual expiration formatting.
     *
     * @since 2.0
     */
    final class Hour implements Expires {

        /**
         * Original time.
         */
        private final Expires origin;

        /**
         * Constructor.
         * @param origin Original time
         */
        Hour(final Expires origin) {
            this.origin = origin;
        }

        @Override
        public String print() {
            return this.origin.print();
        }
    }

    /**
     * Implementation that formats specific expiration dates in GMT.
     *
     * <p>This implementation formats expiration dates using configurable
     * date format patterns, locales, and specific expiration times.
     * It uses SimpleDateFormat with GMT timezone for HTTP-compliant
     * date formatting. The formatting is thread-safe using ThreadLocal
     * to avoid SimpleDateFormat concurrency issues.
     *
     * @since 2.0
     */
    final class Date implements Expires {

        /**
         * DateFormat for expiration.
         */
        private final ThreadLocal<SimpleDateFormat> format;

        /**
         * Expires date.
         */
        private final java.util.Date expires;

        /**
         * Ctor.
         *
         * <p>Will create instance with default format pattern.</p>
         * @param expiration Expiration in millis
         */
        public Date(final long expiration) {
            this("'Expires='EEE, dd MMM yyyy HH:mm:ss z", expiration);
        }

        /**
         * Ctor.
         * @param ptn Date format pattern
         * @param expiration Expiration in millis
         */
        public Date(final String ptn, final long expiration) {
            this(ptn, Locale.ENGLISH, expiration);
        }

        /**
         * Ctor.
         * @param ptn Date format pattern
         * @param locale Locale
         * @param expiration Expiration in millis
         */
        public Date(final String ptn, final Locale locale,
            final long expiration) {
            this(ptn, locale, new java.util.Date(expiration));
        }

        /**
         * Ctor.
         * @param ptn Date format pattern
         * @param locale Locale
         * @param expires Date when expires
         */
        public Date(final String ptn, final Locale locale,
            final java.util.Date expires) {
            this.format = ThreadLocal.withInitial(
                () -> new SimpleDateFormat(ptn, locale)
            );
            this.expires = new java.util.Date(expires.getTime());
        }

        @Override
        public String print() {
            final SimpleDateFormat fmt = this.format.get();
            fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            return fmt.format(this.expires);
        }
    }
}
