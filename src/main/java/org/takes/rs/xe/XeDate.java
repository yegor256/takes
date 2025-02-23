/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs.xe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import lombok.EqualsAndHashCode;
import org.xembly.Directives;

/**
 * Xembly source to create SLA attribute with current date/time in ISO 8601.
 *
 * <p>Add this Xembly source to your page like this:
 *
 * <pre> new RsXembly(
 *   new XsStylesheet("/xsl/home.xsl"),
 *   new XsAppend(
 *     "page",
 *     new XsDate()
 *   )
 * )</pre>
 *
 * <p>And expect this attribute in the XML:
 *
 * <pre>&lt;?xml version="1.0"?&gt;
 * &lt;?xml-stylesheet href="/xsl/home.xsl" type="text/xsl"?&gt;
 * &lt;page date="2015-03-09T00:49:17Z"/&gt;
 * </pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.3
 */
@EqualsAndHashCode(callSuper = true)
public final class XeDate extends XeWrap {

    /**
     * Ctor.
     */
    public XeDate() {
        this("date");
    }

    /**
     * Ctor.
     * @param attr Attribute name
     */
    public XeDate(final CharSequence attr) {
        super(
            () -> {
                final DateFormat fmt = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH
                );
                fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
                return new Directives().attr(
                    attr.toString(), fmt.format(new Date())
                );
            }
        );
    }

}
