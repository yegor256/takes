/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Mac;
import org.takes.facets.auth.Identity;

/**
 * MAC (Message Authentication Code) codec that signs identity data with
 * a provided algorithm and secret key to ensure integrity and authenticity.
 *
 * <p>This codec decorator adds cryptographic signatures to identity data
 * using HMAC or other MAC algorithms. During encoding, it appends a MAC
 * signature to the encoded data. During decoding, it verifies the signature
 * before returning the decoded identity, throwing an IOException if the
 * signature is invalid.
 *
 * <p>The resulting format is: [encoded_data][mac_signature] where the
 * MAC signature length depends on the algorithm used (e.g., 32 bytes for
 * HMAC-SHA256).
 *
 * <p>Usage example:
 * <pre> {@code
 * final Key key = new SecretKeySpec("secret".getBytes(), "HmacSHA256");
 * final Codec codec = new CcSigned(new CcPlain(), "HmacSHA256", key);
 * final Identity identity = new Identity.Simple("urn:user:john", props);
 * final byte[] encoded = codec.encode(identity); // signed data
 * final Identity decoded = codec.decode(encoded); // verified
 * }</pre>
 *
 * @since 1.11.1
 */
public final class CcSigned implements Codec {
    /**
     * Origin codec.
     */
    private final Codec cdc;

    /**
     * MAC algorithm.
     */
    private final String alg;

    /**
     * Secret key.
     */
    private final Key key;

    /**
     * Ctor.
     * @param origin Origin codec
     * @param algorithm Algorithm name
     * @param secret Secret key
     */
    public CcSigned(
        final Codec origin,
        final String algorithm,
        final Key secret
    ) {
        this.cdc = origin;
        this.alg = algorithm;
        this.key = secret;
    }

    @Override
    public byte[] encode(final Identity identity) throws IOException {
        final byte[] encoded = this.cdc.encode(identity);
        final byte[] signature = this.mac().doFinal(encoded);
        final byte[] signed = new byte[encoded.length + signature.length];
        System.arraycopy(encoded, 0, signed, 0, encoded.length);
        System.arraycopy(
            signature,
            0,
            signed,
            encoded.length,
            signature.length
        );
        return signed;
    }

    @Override
    public Identity decode(final byte[] bytes) throws IOException {
        final Mac mac = this.mac();
        if (bytes.length < mac.getMacLength()) {
            throw new IOException("Invalid data size");
        }
        final byte[] signature = new byte[mac.getMacLength()];
        final byte[] encoded = new byte[bytes.length - signature.length];
        System.arraycopy(bytes, 0, encoded, 0, encoded.length);
        System.arraycopy(
            bytes,
            encoded.length,
            signature,
            0,
            signature.length
        );
        final byte[] actual = mac.doFinal(encoded);
        if (!Arrays.equals(actual, signature)) {
            throw new IOException("Bad signature");
        }
        return this.cdc.decode(encoded);
    }

    /**
     * Obtain MAC instance.
     * @return Initialized MAC
     * @throws IOException If algorithm missed or invalid key
     */
    private Mac mac() throws IOException {
        try {
            final Mac mac = Mac.getInstance(this.alg);
            mac.init(this.key);
            return mac;
        } catch (final NoSuchAlgorithmException | InvalidKeyException err) {
            throw new IOException(err);
        }
    }
}
