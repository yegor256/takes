/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs.xe;

import lombok.EqualsAndHashCode;
import org.xembly.Directives;

/**
 * Xembly source to report memory usage.
 *
 * <p>Add this Xembly source to your page like this:
 *
 * <pre> new RsXembly(
 *   new XsStylesheet("/xsl/home.xsl"),
 *   new XsAppend(
 *     "page",
 *     new XsMemory()
 *   )
 * )</pre>
 *
 * <p>And expect this attribute in the XML:
 *
 * <pre>&lt;?xml version="1.0"?&gt;
 * &lt;?xml-stylesheet href="/xsl/home.xsl" type="text/xsl"?&gt;
 * &lt;page&gt;
 * &lt;memory&gt;free="1344" max="2048" total="334"&lt;/memory&gt;
 * &lt;/page&gt;
 * </pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 1.2
 */
@EqualsAndHashCode(callSuper = true)
public final class XeMemory extends XeWrap {

    /**
     * Ctor.
     */
    public XeMemory() {
        this("memory");
    }

    /**
     * Ctor.
     * @param node Node name
     */
    public XeMemory(final CharSequence node) {
        super(
            () -> new Directives().add(node.toString())
                .attr(
                    "total",
                    XeMemory.mbs(Runtime.getRuntime().totalMemory())
                )
                .attr(
                    "free",
                    XeMemory.mbs(Runtime.getRuntime().freeMemory())
                )
                .attr(
                    "max",
                    XeMemory.mbs(Runtime.getRuntime().maxMemory())
                )
        );
    }

    /**
     * Format memory.
     * @param bytes Bytes
     * @return Mbytes
     */
    private static long mbs(final long bytes) {
        return bytes >> 20;
    }

}
