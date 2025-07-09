/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.takes.facets.auth.Identity;

/**
 * Gzip codec.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.16
 */
public final class CcGzip implements Codec {

    /**
     * Original codec.
     */
    private final Codec origin;

    /**
     * Ctor.
     * @param codec Original
     */
    public CcGzip(final Codec codec) {
        this.origin = codec;
    }

    @Override
    public byte[] encode(final Identity identity) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(this.origin.encode(identity));
        }
        return out.toByteArray();
    }

    @Override
    public Identity decode(final byte[] bytes) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (InputStream gzip = new GZIPInputStream(
            new ByteArrayInputStream(bytes)
            )
        ) {
            while (true) {
                final int data = gzip.read();
                if (data < 0) {
                    break;
                }
                out.write(data);
            }
        }
        return this.origin.decode(out.toByteArray());
    }

    /**
     * Check for equality.
     *
     * @param o The object to check
     * @return True if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CcGzip ccGzip = (CcGzip) o;
        return Objects.equals(origin, ccGzip.origin);
    }

    /**
     * Calculate hash code.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(origin);
    }

}
