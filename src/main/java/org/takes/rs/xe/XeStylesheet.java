/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
