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
