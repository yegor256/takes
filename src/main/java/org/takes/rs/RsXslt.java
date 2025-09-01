/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.io.InputStreamOf;
import org.cactoos.io.ReaderOf;
import org.cactoos.io.WriterTo;
import org.cactoos.scalar.Unchecked;
import org.takes.Response;

/**
 * Response decorator that transforms XML to HTML using XSL stylesheets.
 *
 * <p>This decorator processes XML responses that contain XSL stylesheet
 * processing instructions and transforms them into HTML or other formats.
 * The stylesheet location is resolved using a configurable URIResolver,
 * with classpath resolution as the default. Transformer factories are
 * cached for performance.
 *
 * <p>Expected XML format:
 * <pre>&lt;?xml version="1.0"?&gt;
 * &lt;?xml-stylesheet href="/xsl/home.xsl" type="text/xsl"?&gt;
 * &lt;page/&gt;
 * </pre>
 *
 * <p>The stylesheet {@code /xsl/home.xsl} will be resolved on the classpath.
 * If not found, a runtime exception is thrown.
 *
 * <p>Example usage with RsXembly:
 * <pre>new RsXslt(
 *   new RsXembly(
 *     new XeStylesheet("/xsl/home.xsl"),
 *     new XeAppend(
 *       "page",
 *       new XeDate(),
 *       new XeLocalhost(),
 *       new XeSLA()
 *     )
 *   )
 * )</pre>
 *
 * <p><strong>Note:</strong> Saxon is recommended as the XSL transformer
 * for best compatibility and performance.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @see org.takes.rs.xe.RsXembly
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsXslt extends RsWrap {

    /**
     * Cached factory.
     */
    private static final Map<URIResolver, TransformerFactory> FACTORIES =
        new ConcurrentHashMap<>(0);

    /**
     * Ctor.
     * @param rsp Original response
     */
    public RsXslt(final Response rsp) {
        this(rsp, new RsXslt.InClasspath());
    }

    /**
     * Ctor.
     * @param rsp Original response
     * @param resolver URI resolver
     */
    public RsXslt(final Response rsp, final URIResolver resolver) {
        super(
            new RsWithHeader(
                new ResponseOf(
                    rsp::head,
                    () -> RsXslt.transform(rsp.body(), resolver)
                ),
                () -> String.format(
                    "X-Takes-RsXslt-TransformerFactory: %s",
                    RsXslt.factory(resolver).getClass().getCanonicalName()
                )
            )
        );
    }

    /**
     * Get factory for the given resolver.
     * @param resolver Resolver
     * @return Factory
     */
    private static TransformerFactory factory(final URIResolver resolver) {
        return RsXslt.FACTORIES.computeIfAbsent(
            resolver,
            res -> {
                final TransformerFactory fct = TransformerFactory.newInstance();
                fct.setURIResolver(res);
                new Unchecked<>(
                    () -> {
                        fct.setFeature(
                            XMLConstants.FEATURE_SECURE_PROCESSING,
                            true
                        );
                        return 0;
                    }).value();
                return fct;
            }
        );
    }

    /**
     * Build body.
     * @param origin Original body
     * @param resolver Resolver
     * @return Body
     * @throws IOException If fails
     */
    private static InputStream transform(final InputStream origin,
        final URIResolver resolver) throws IOException {
        final TransformerFactory fct = RsXslt.factory(resolver);
        try {
            return RsXslt.transform(fct, origin);
        } catch (final TransformerException ex) {
            throw new IOException(
                String.format(
                    "Can't transform via %s",
                    fct.getClass().getName()
                ),
                ex
            );
        }
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
            input = RsXslt.consume(xml);
        } catch (final IOException ex) {
            throw new IllegalStateException(
                "Failed to consume XML by XSLT",
                ex
            );
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Source xsl = RsXslt.stylesheet(
            factory,
            new StreamSource(new ReaderOf(input))
        );
        RsXslt.transformer(factory, xsl).transform(
            new StreamSource(
                new ReaderOf(input)
            ),
            new StreamResult(
                new WriterTo(baos)
            )
        );
        return new InputStreamOf(baos.toByteArray());
    }

    /**
     * Consume input stream.
     * @param input Input stream
     * @return Bytes found
     * @throws IOException If fails
     */
    private static byte[] consume(final InputStream input) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] buf = new byte[4096];
        try (InputStream stream = input) {
            while (true) {
                final int bytes = stream.read(buf);
                if (bytes < 0) {
                    break;
                }
                baos.write(buf, 0, bytes);
            }
        }
        return baos.toByteArray();
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
                "No associated stylesheet found in XML"
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
     * @since 0.1
     */
    private static final class InClasspath implements URIResolver {
        @Override
        public Source resolve(final String href, final String base)
            throws TransformerException {
            final URI uri;
            if (base == null || base.isEmpty()) {
                uri = URI.create(href);
            } else {
                uri = URI.create(base).resolve(href);
            }
            final InputStream input;
            if (uri.isAbsolute() && !"file".equals(uri.getScheme())) {
                try {
                    input = uri.toURL().openStream();
                } catch (final IOException ex) {
                    throw new IllegalStateException(
                        String.format("Failed to open URL '%s'", uri),
                        ex
                    );
                }
            } else {
                input = this.getClass().getResourceAsStream(uri.getPath());
                if (input == null) {
                    throw new TransformerException(
                        String.format(
                            "\"%s\" not found in classpath, base=\"%s\"",
                            href, base
                        )
                    );
                }
            }
            return new StreamSource(
                new ReaderOf(input)
            );
        }
    }

}
