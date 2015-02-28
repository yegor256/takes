/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.EqualsAndHashCode;
import org.takes.Response;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWithType;
import org.w3c.dom.Node;
import org.xembly.Xembler;

/**
 * Response that converts Xembly object to XML.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@EqualsAndHashCode(of = "source")
public final class RsXembly implements Response {

    /**
     * Xembly source.
     */
    private final transient XeSource source;

    /**
     * Ctor.
     * @param sources Sources
     */
    public RsXembly(final XeSource... sources) {
        this(Arrays.asList(sources));
    }

    /**
     * Ctor.
     * @param sources Sources
     */
    public RsXembly(final Iterable<XeSource> sources) {
        this(new XeChain(sources));
    }

    /**
     * Ctor.
     * @param src Source
     */
    public RsXembly(final XeSource src) {
        this.source = src;
    }

    @Override
    public List<String> head() throws IOException {
        return new RsWithType(
            new RsWithStatus(new RsEmpty(), HttpURLConnection.HTTP_OK),
            "text/xml"
        ).head();
    }

    @Override
    public InputStream body() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Node node = new Xembler(this.source.toXembly()).domQuietly();
        try {
            TransformerFactory.newInstance().newTransformer().transform(
                new DOMSource(node),
                new StreamResult(new OutputStreamWriter(baos))
            );
        } catch (final TransformerException ex) {
            throw new IllegalStateException(ex);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

}
