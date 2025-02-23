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
 * Xembly source to create an XSL stylesheet processing instruction.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 * @see <a href="http://www.yegor256.com/2014/06/25/xml-and-xslt-in-browser.html">XML+XSLT in a Browser</a>
 * @see <a href="http://www.yegor256.com/2014/09/09/restful-web-sites.html">RESTful API and a Web Site in the Same URL</a>
 */
@EqualsAndHashCode(callSuper = true)
public final class XeStylesheet extends XeWrap {

    /**
     * Ctor.
     *
     * <p>The only argument here is a location of XSL stylesheet,
     * in resources and, at the same time, in HTTP. For example, you
     * can put "/xsl/main.xsl" there. In the XML output this will generate
     * an "xml-stylesheet" annotation, which will point the browser
     * to "http://yoursite/xsl/main.xsl". Obviously, this page must
     * be available. On the other hand, if you're using RsXSLT, this
     * file must be available in resources as "/xsl/main.xsl".
     *
     * <p>It is recommended to put XSL files under "src/main/resources/xsl".
     *
     * @param xsl XSL stylesheet
     */
    public XeStylesheet(final CharSequence xsl) {
        super(
            () -> new Directives().pi(
                "xml-stylesheet",
                String.format("href='%s' type='text/xsl'", xsl)
            )
        );
    }

}
