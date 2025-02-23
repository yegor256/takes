/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs.xe;

import lombok.EqualsAndHashCode;
import org.xembly.Directives;

/**
 * Xembly source to create "lifetime" attribute at the root, in milliseconds.
 *
 * <p>Add this Xembly source to your page like this:
 *
 * <pre> new RsXembly(
 *   new XeStylesheet("/xsl/home.xsl"),
 *   new XeLifetime()
 * )</pre>
 *
 * <p>And expect this attribute in the XML:
 *
 * <pre>&lt;?xml version="1.0"?&gt;
 * &lt;?xml-stylesheet href="/xsl/home.xsl" type="text/xsl"?&gt;
 * &lt;page lifetime="473243289"&gt;
 * &lt;/page&gt;
 * </pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 1.7
 */
@EqualsAndHashCode(callSuper = true)
public final class XeLifetime extends XeWrap {

    /**
     * Time of start.
     */
    private static final long START = System.currentTimeMillis();

    /**
     * Ctor.
     */
    public XeLifetime() {
        this("lifetime");
    }

    /**
     * Ctor.
     * @param elm Element name
     */
    public XeLifetime(final CharSequence elm) {
        super(
            () -> new Directives().attr(
                elm.toString(),
                Long.toString(
                    System.currentTimeMillis() - XeLifetime.START
                )
            )
        );
    }
}
