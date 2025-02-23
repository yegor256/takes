/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs.xe;

import java.lang.management.ManagementFactory;
import java.util.Locale;
import lombok.EqualsAndHashCode;
import org.xembly.Directives;

/**
 * Xembly source to create SLA attribute with server load average.
 *
 * <p>Add this Xembly source to your page like this:
 *
 * <pre> new RsXembly(
 *   new XsStylesheet("/xsl/home.xsl"),
 *   new XsAppend(
 *     "page",
 *     new XsSLA()
 *   )
 * )</pre>
 *
 * <p>And expect this attribute in the XML:
 *
 * <pre>&lt;?xml version="1.0"?&gt;
 * &lt;?xml-stylesheet href="/xsl/home.xsl" type="text/xsl"?&gt;
 * &lt;page sla="1.908"/&gt;
 * </pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.3
 */
@EqualsAndHashCode(callSuper = true)
public final class XeSla extends XeWrap {

    /**
     * Ctor.
     */
    public XeSla() {
        this("sla");
    }

    /**
     * Ctor.
     * @param attr Attribute name
     */
    public XeSla(final CharSequence attr) {
        super(
            () -> new Directives().attr(
                attr.toString(),
                String.format(
                    Locale.US,
                    "%.3f",
                    ManagementFactory.getOperatingSystemMXBean()
                        .getSystemLoadAverage()
                )
            )
        );
    }

}
