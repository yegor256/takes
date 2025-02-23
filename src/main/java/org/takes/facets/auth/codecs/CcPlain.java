/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import org.cactoos.Text;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.list.ListOf;
import org.cactoos.text.Split;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.takes.facets.auth.Identity;

/**
 * Plain codec.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode
public final class CcPlain implements Codec {

    @Override
    public byte[] encode(final Identity identity) throws IOException {
        final String encoding = Charset.defaultCharset().name();
        final StringBuilder text = new StringBuilder(
            URLEncoder.encode(identity.urn(), encoding)
        );
        for (final Map.Entry<String, String> ent
            : identity.properties().entrySet()) {
            text.append(';')
                .append(ent.getKey())
                .append('=')
                .append(URLEncoder.encode(ent.getValue(), encoding));
        }
        return new UncheckedBytes(
            new BytesOf(text)
        ).asBytes();
    }

    @Override
    public Identity decode(final byte[] bytes) throws IOException {
        final List<Text> parts = new ListOf<>(
            new Split(
                new TextOf(bytes), ";"
            )
        );
        final Map<String, String> map = new HashMap<>(parts.size());
        for (int idx = 1; idx < parts.size(); ++idx) {
            final List<Text> pair = new ListOf<>(
                new Split(parts.get(idx), "=")
            );
            try {
                map.put(
                    new UncheckedText(pair.get(0)).asString(),
                    CcPlain.decode(new UncheckedText(pair.get(1)).asString())
                );
            } catch (final IllegalArgumentException ex) {
                throw new DecodingException(ex);
            }
        }
        return new Identity.Simple(
            CcPlain.decode(
                new UncheckedText(parts.get(0)).asString()
            ), map
        );
    }

    /**
     * Decode from URL.
     * @param text The text
     * @return Decoded
     * @throws UnsupportedEncodingException If fails
     */
    private static String decode(final String text)
        throws UnsupportedEncodingException {
        try {
            return URLDecoder.decode(text, Charset.defaultCharset().name());
        } catch (final IllegalArgumentException ex) {
            throw new DecodingException(ex);
        }
    }

}
