package org.takes.misc;


import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public interface Expires {

    /**
     * String representation of expiration time.
     * @return
     */
    String print();

    /**
     * Never expires.
     */
    final class Never implements Expires {
        @Override
        public String print() {
            throw new UnsupportedOperationException("print() not implemented");
        }
    }

    /**
     * Expires in one hour of the given time.
     */
    final class Hour implements Expires {

        /**
         * Original time.
         */
        private final Expires origin;

        Hour(final Expires origin) {
            this.origin = origin;
        }

        @Override
        public String print() {
            throw new UnsupportedOperationException("print() not implemented");
        }
    }

    /**
     * Expiration date in GMT.
     *
     * @author Paulo Lobo (pauloeduardolobo@gmail.com)
     * @version $$
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
            this.format = new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    return new SimpleDateFormat(ptn, locale);
                }
            };
            this.expires = expires;
        }

        @Override
        public String print() {
            final SimpleDateFormat fmt = this.format.get();
            fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            return fmt.format(this.expires);
        }
    }
}
