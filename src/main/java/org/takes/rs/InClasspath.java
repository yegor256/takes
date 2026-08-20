/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import org.cactoos.io.ReaderOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;

/**
 * Classpath URI resolver.
 * @since 0.1
 */
final class InClasspath implements URIResolver {

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
                    new UncheckedText(
                        new FormattedText(
                            "Failed to open URL '%s'", uri
                        )
                    ).asString(),
                    ex
                );
            }
        } else {
            input = this.getClass().getResourceAsStream(uri.getPath());
            if (input == null) {
                throw new TransformerException(
                    new UncheckedText(
                        new FormattedText(
                            "\"%s\" not found in classpath, base=\"%s\"",
                            href, base
                        )
                    ).asString()
                );
            }
        }
        return new StreamSource(
            new ReaderOf(input)
        );
    }
}
