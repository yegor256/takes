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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Expiration date in GMT.
 *
 * @author Izbassar Tolegen (t.izbassar@gmail.com)
 * @version $Id$
 * @since 2.0
 */
public final class ExpirationDate {

    /**
     * DateFormat for expiration.
     */
    private final ThreadLocal<SimpleDateFormat> format;

    /**
     * Expires date.
     */
    private final Date expires;

    /**
     * Ctor.
     *
     * <p>Will create instance with default format pattern.</p>
     * @param expiration Expiration in millis
     */
    public ExpirationDate(final long expiration) {
        this("'Expires='EEE, dd MMM yyyy HH:mm:ss z", expiration);
    }

    /**
     * Ctor.
     * @param ptn Date format pattern
     * @param expiration Expiration in millis
     */
    public ExpirationDate(final String ptn, final long expiration) {
        this(ptn, Locale.ENGLISH, expiration);
    }

    /**
     * Ctor.
     * @param ptn Date format pattern
     * @param locale Locale
     * @param expiration Expiration in millis
     */
    public ExpirationDate(final String ptn, final Locale locale,
        final long expiration) {
        this(ptn, locale, new Date(expiration));
    }

    /**
     * Ctor.
     * @param ptn Date format pattern
     * @param locale Locale
     * @param expires Date when expires
     */
    public ExpirationDate(final String ptn, final Locale locale,
        final Date expires) {
        this.format = new ThreadLocal<SimpleDateFormat>() {
            @Override
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat(ptn, locale);
            }
        };
        this.expires = expires;
    }

    @Override
    public String toString() {
        final SimpleDateFormat fmt = this.format.get();
        fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return fmt.format(this.expires);
    }
}
