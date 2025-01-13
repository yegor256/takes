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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.io.WriterTo;
import org.takes.rs.ResponseOf;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWithType;
import org.takes.rs.RsWrap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xembly.Xembler;

/**
 * Response that converts Xembly object to XML.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsXembly extends RsWrap {

    /**
     * Ctor.
     * @param sources Sources
     */
    public RsXembly(final XeSource... sources) {
        this(Arrays.asList(sources));
    }

    /**
     * Ctor.
     * @param dom DOM node to build upon
     * @param sources Sources
     */
    public RsXembly(final Node dom, final XeSource... sources) {
        this(dom, Arrays.asList(sources));
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
     * @param dom DOM node to build upon
     * @param sources Sources
     */
    public RsXembly(final Node dom, final Iterable<XeSource> sources) {
        this(dom, new XeChain(sources));
    }

    /**
     * Ctor.
     * @param src Source
     */
    public RsXembly(final XeSource src) {
        this(RsXembly.emptyDocument(), src);
    }

    /**
     * Ctor.
     * @param dom DOM node to build upon
     * @param src Source
     */
    public RsXembly(final Node dom, final XeSource src) {
        super(
            new ResponseOf(
                () -> new RsWithType(
                    new RsWithStatus(
                        new RsEmpty(), HttpURLConnection.HTTP_OK
                    ), "text/xml"
                ).head(),
                () -> RsXembly.render(dom, src)
            )
        );
    }

    /**
     * Render source as XML.
     * @param dom DOM node to build upon
     * @param src Source
     * @return XML
     * @throws IOException If fails
     */
    private static InputStream render(final Node dom,
        final XeSource src) throws IOException {
        final Node copy = cloneNode(dom);
        final ByteArrayOutputStream baos =
            new ByteArrayOutputStream();
        final Node node = new Xembler(src.toXembly()).applyQuietly(copy);
        try {
            TransformerFactory.newInstance().newTransformer().transform(
                new DOMSource(node),
                new StreamResult(
                    new WriterTo(baos)
                )
            );
        } catch (final TransformerException ex) {
            throw new IllegalStateException(
                "Failed to transform XML via XSLT",
                ex
            );
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * Create empty DOM Document.
     * @return Document
     */
    private static Document emptyDocument() {
        try {
            return DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .newDocument();
        } catch (final ParserConfigurationException ex) {
            throw new IllegalStateException(
                "Could not instantiate DocumentBuilderFactory and build empty Document",
                ex
            );
        }
    }

    /**
     * Create Node clone.
     * @param dom Node to clone
     * @return Cloned Node
     */
    private static Node cloneNode(final Node dom) {
        final Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (final TransformerConfigurationException ex) {
            throw new IllegalStateException(
                "Could not create new Transformer to clone Node",
                ex
            );
        }
        final DOMSource source = new DOMSource(dom);
        try {
            final DOMResult result = new DOMResult();
            transformer.transform(source, result);
            return result.getNode();
        } catch (final TransformerException ex) {
            throw new IllegalArgumentException(
                String.format(
                    "Could not clone Node %s with Transformer %s",
                    source,
                    transformer
                ),
                ex
            );
        }
    }
}
