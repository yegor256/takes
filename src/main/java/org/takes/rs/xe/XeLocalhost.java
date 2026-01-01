/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs.xe;

import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.EqualsAndHashCode;
import org.xembly.Directives;

/**
 * Xembly source to create SLA attribute with server IP address.
 *
 * <p>Add this Xembly source to your page like this:
 *
 * <pre> new RsXembly(
 *   new XsStylesheet("/xsl/home.xsl"),
 *   new XsAppend(
 *     "page",
 *     new XsLocalhost()
 *   )
 * )</pre>
 *
 * <p>And expect this attribute in the XML:
 *
 * <pre>&lt;?xml version="1.0"?&gt;
 * &lt;?xml-stylesheet href="/xsl/home.xsl" type="text/xsl"?&gt;
 * &lt;page ip="172.18.183.14"/&gt;
 * </pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.3
 */
@EqualsAndHashCode(callSuper = true)
public final class XeLocalhost extends XeWrap {

    /**
     * Ctor.
     */
    public XeLocalhost() {
        this("ip");
    }

    /**
     * Ctor.
     * @param attr Attribute name
     */
    @SuppressWarnings(
        {
            "PMD.CallSuperInConstructor",
            "PMD.ConstructorOnlyInitializesOrCallOtherConstructors"
        }
    )
    public XeLocalhost(final CharSequence attr) {
        super(
            () -> {
                String addr;
                try {
                    addr = InetAddress.getLocalHost().getHostAddress();
                } catch (final UnknownHostException ex) {
                    addr = ex.getClass().getCanonicalName();
                }
                return new Directives().attr(attr.toString(), addr);
            }
        );
    }

}
