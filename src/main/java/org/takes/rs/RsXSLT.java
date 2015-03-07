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
package org.takes.rs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import lombok.EqualsAndHashCode;
import org.takes.Response;

/**
 * Response that converts XML into HTML using attached XSL stylesheet.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@EqualsAndHashCode(of = { "origin", "resolver" })
public final class RsXSLT implements Response {

    /**
     * Original response.
     */
    private final transient Response origin;

    /**
     * URI resolver.
     */
    private final transient URIResolver resolver;

    /**
     * Ctor.
     * @param rsp Original response
     */
    public RsXSLT(final Response rsp) {
        this(rsp, new RsXSLT.InClasspath());
    }

    /**
     * Ctor.
     * @param rsp Original response
     * @param rslv URI resolver
     */
    public RsXSLT(final Response rsp, final URIResolver rslv) {
        this.origin = rsp;
        this.resolver = rslv;
    }

    @Override
    public List<String> head() throws IOException {
        return this.origin.head();
    }

    @Override
    public InputStream body() throws IOException {
        try {
            final TransformerFactory factory = TransformerFactory.newInstance();
            factory.setURIResolver(this.resolver);
            return RsXSLT.transform(factory, this.origin.body());
        } catch (final TransformerException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Consume input stream.
     * @param input Input stream
     * @return Bytes found
     * @throws IOException If fails
     */
    private static byte[] consume(final InputStream input) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (true) {
            final int data = input.read();
            if (data < 0) {
                break;
            }
            baos.write(data);
        }
        return baos.toByteArray();
    }

    /**
     * Transform XML into HTML.
     * @param factory Transformer factory
     * @param xml XML page to be transformed.
     * @return Resulting HTML page.
     * @throws TransformerException If fails
     */
    private static InputStream transform(final TransformerFactory factory,
        final InputStream xml) throws TransformerException {
        final byte[] input;
        try {
            input = RsXSLT.consume(xml);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Source xsl = RsXSLT.stylesheet(
            factory, new StreamSource(
                new InputStreamReader(new ByteArrayInputStream(input))
            )
        );
        RsXSLT.transformer(factory, xsl).transform(
            new StreamSource(
                new InputStreamReader(new ByteArrayInputStream(input))
            ),
            new StreamResult(new OutputStreamWriter(baos))
        );
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * Retrieve a stylesheet from this XML (throws an exception if
     * no stylesheet is attached).
     * @param factory Transformer factory
     * @param xml The XML
     * @return Stylesheet found
     * @throws TransformerConfigurationException If fails
     */
    private static Source stylesheet(final TransformerFactory factory,
        final Source xml) throws TransformerConfigurationException {
        final Source stylesheet = factory.getAssociatedStylesheet(
            xml, null, null, null
        );
        if (stylesheet == null) {
            throw new IllegalArgumentException(
                "no associated stylesheet found in XML"
            );
        }
        return stylesheet;
    }

    /**
     * Make a transformer from this stylesheet.
     * @param factory Transformer factory
     * @param stylesheet The stylesheet
     * @return Transformer
     * @throws TransformerConfigurationException If fails
     */
    private static Transformer transformer(final TransformerFactory factory,
        final Source stylesheet) throws TransformerConfigurationException {
        final Transformer tnfr = factory.newTransformer(stylesheet);
        if (tnfr == null) {
            throw new TransformerConfigurationException(
                String.format(
                    "%s failed to create new XSL transformer for '%s'",
                    factory.getClass(),
                    stylesheet.getSystemId()
                )
            );
        }
        return tnfr;
    }

    /**
     * Classpath URI resolver.
     */
    private static final class InClasspath implements URIResolver {
        @Override
        public Source resolve(final String href, final String base)
            throws TransformerException {
            final InputStream input = this.getClass().getResourceAsStream(href);
            if (input == null) {
                throw new TransformerException(
                    String.format("XSL '%s' not found in classpath", href)
                );
            }
            return new StreamSource(new InputStreamReader(input));
        }
    }

}
