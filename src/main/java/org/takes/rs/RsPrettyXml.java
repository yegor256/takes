/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import lombok.ToString;
import org.cactoos.scalar.And;
import org.cactoos.scalar.HashCode;
import org.cactoos.scalar.Or;
import org.cactoos.scalar.Unchecked;
import org.takes.Response;
import org.w3c.dom.DocumentType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Response with properly indented XML body.
 *
 * <p>The class is immutable and thread-safe.
 * @since 1.0
 */
@ToString(of = "origin")
public final class RsPrettyXml implements Response {

    /**
     * Xerces feature to disable external DTD validation.
     */
    private static final String LOAD_EXTERNAL_DTD =
        "http://apache.org/xml/features/nonvalidating/load-external-dtd";

    /**
     * Original response.
     */
    private final Response origin;

    /**
     * Response with properly transformed body.
     */
    private final List<Response> transformed;

    /**
     * Synchronization lock.
     */
    private final Object lock;

    /**
     * Ctor.
     * @param res Original response
     */
    public RsPrettyXml(final Response res) {
        this.transformed = new CopyOnWriteArrayList<>();
        this.origin = res;
        this.lock = new Object();
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.make().head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.make().body();
    }

    @Override
    @SuppressFBWarnings("EQ_UNUSUAL")
    public boolean equals(final Object that) {
        return new Unchecked<>(
            new Or(
                () -> this == that,
                new And(
                    () -> that != null,
                    () -> RsPrettyXml.class.equals(that.getClass()),
                    () -> {
                        final RsPrettyXml other = (RsPrettyXml) that;
                        return new And(
                            () -> this.origin.equals(other.origin),
                            () -> this.transformed.equals(other.transformed)
                        ).value();
                    }
                )
            )
        ).value();
    }

    @Override
    public int hashCode() {
        return new HashCode(this.origin, this.transformed).value();
    }

    /**
     * Make a response.
     * @return Response just made
     * @throws IOException If fails
     */
    private Response make() throws IOException {
        synchronized (this.lock) {
            if (this.transformed.isEmpty()) {
                this.transformed.add(
                    new RsWithBody(
                        this.origin,
                        RsPrettyXml.transform(this.origin.body())
                    )
                );
            }
        }
        return this.transformed.get(0);
    }

    /**
     * Format body with proper indents using SAX.
     * @param body Response body
     * @return New properly formatted body
     * @throws IOException If fails
     */
    private static byte[] transform(final InputStream body) throws IOException {
        final SAXSource source = new SAXSource(new InputSource(body));
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            final XMLReader xmlreader = SAXParserFactory.newInstance()
                .newSAXParser().getXMLReader();
            source.setXMLReader(xmlreader);
            xmlreader.setFeature(
                RsPrettyXml.LOAD_EXTERNAL_DTD, false
            );
            final String yes = "yes";
            final Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();
            transformer.setOutputProperty(
                OutputKeys.OMIT_XML_DECLARATION, yes
            );
            RsPrettyXml.prepareDocType(body, transformer);
            transformer.setOutputProperty(OutputKeys.INDENT, yes);
            transformer.transform(source, new StreamResult(result));
        } catch (final TransformerException
            | ParserConfigurationException
            | SAXException ex) {
            throw new IOException(ex);
        }
        return result.toByteArray();
    }

    /**
     * Parses body to get DOCTYPE and configure Transformer
     * with proper method, public id and system id.
     * @param body The body to be parsed.
     * @param transformer Transformer to configure with proper properties.
     * @throws IOException if something goes wrong.
     */
    private static void prepareDocType(final InputStream body,
        final Transformer transformer) throws IOException {
        try {
            final String html = "html";
            final DocumentType doctype = RsPrettyXml.getDocType(body);
            if (null != doctype) {
                if (null == doctype.getSystemId()
                    && null == doctype.getPublicId()
                    && html.equalsIgnoreCase(doctype.getName())) {
                    transformer.setOutputProperty(OutputKeys.METHOD, html);
                    transformer.setOutputProperty(OutputKeys.VERSION, "5.0");
                    return;
                }
                if (null != doctype.getSystemId()) {
                    transformer.setOutputProperty(
                        OutputKeys.DOCTYPE_SYSTEM,
                        doctype.getSystemId()
                    );
                }
                if (null != doctype.getPublicId()) {
                    transformer.setOutputProperty(
                        OutputKeys.DOCTYPE_PUBLIC,
                        doctype.getPublicId()
                    );
                }
            }
        } finally {
            body.reset();
        }
    }

    /**
     * Parses the input stream and returns DocumentType built without loading
     * any external DTD schemas.
     * @param body The body to be parsed.
     * @return The documents DocumentType.
     * @throws IOException if something goes wrong.
     */
    private static DocumentType getDocType(final InputStream body)
        throws IOException {
        final DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
        try {
            factory.setFeature(RsPrettyXml.LOAD_EXTERNAL_DTD, false);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(body).getDoctype();
        } catch (final ParserConfigurationException | SAXException ex) {
            throw new IOException(ex);
        }
    }
}
