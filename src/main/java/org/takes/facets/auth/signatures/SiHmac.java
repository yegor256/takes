/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.signatures;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.EqualsAndHashCode;

/**
 * An HMAC signature implementation that supports 256, 384, and 512-bit hash variants.
 *
 * <p>This class provides HMAC (Hash-based Message Authentication Code) signature
 * functionality using SHA-256, SHA-384, or SHA-512 algorithms. It creates hex-encoded
 * signatures from input data using a secret key. The class is immutable and thread-safe.
 *
 * @since 1.4
 */
@EqualsAndHashCode
public final class SiHmac implements Signature {
    /**
     * The HMAC 256 bit variant.
     */
    public static final int HMAC256 = 256;

    /**
     * The HMAC 384 bit variant.
     */
    public static final int HMAC384 = 384;

    /**
     * The HMAC 512 bit variant.
     */
    public static final int HMAC512 = 512;

    /**
     * The encryption key.
     */
    private final byte[] key;

    /**
     * The bit length parameter.
     */
    private final int bits;

    /**
     * Constructor with string key using default 256-bit HMAC.
     *
     * @param key The encryption key as a string
     */
    public SiHmac(final String key) {
        this(key.getBytes(StandardCharsets.UTF_8), SiHmac.HMAC256);
    }

    /**
     * Constructor with string key and specified bit length.
     *
     * @param key The encryption key as a string
     * @param bits The signature bit length (256, 384, or 512)
     */
    public SiHmac(final String key, final int bits) {
        this(key.getBytes(StandardCharsets.UTF_8), bits);
    }

    /**
     * Constructor with byte array key and specified bit length.
     *
     * @param key The encryption key as a byte array
     * @param bits The signature bit length (256, 384, or 512)
     */
    public SiHmac(final byte[] key, final int bits) {
        this.key = key.clone();
        this.bits = SiHmac.bitLength(bits);
    }

    @Override
    public byte[] sign(final byte[] data) throws IOException {
        return this.encrypt(data);
    }

    /**
     * Returns the signature bit length.
     *
     * @return The bit length used for HMAC signature
     */
    public int bitlength() {
        return this.bits;
    }

    /**
     * Validates and returns the correct bit length.
     *
     * @param bits The given bit length
     * @return The original bit length if valid (256, 384, or 512), otherwise 256
     */
    private static int bitLength(final int bits) {
        int correct = bits;
        if (bits != SiHmac.HMAC256
            && bits != SiHmac.HMAC384
            && bits != SiHmac.HMAC512) {
            correct = SiHmac.HMAC256;
        }
        return correct;
    }

    /**
     * Encrypts the given bytes using HMAC and returns hex-encoded result.
     *
     * @param bytes The bytes to encrypt
     * @return The encrypted bytes as hex-encoded string bytes
     * @throws IOException If encryption fails
     */
    private byte[] encrypt(final byte[] bytes) throws IOException {
        final byte[] result = this.create().doFinal(bytes);
        final char[] hex = new char[result.length * 2];
        for (int idx = 0; idx < result.length; idx = idx + 1) {
            final int val = result[idx] & 0xff;
            hex[idx * 2] = Character.forDigit(val >>> 4, 16);
            hex[idx * 2 + 1] = Character.forDigit(val & 0x0f, 16);
        }
        return new String(hex).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Creates a new MAC instance based on the configured bit length.
     *
     * @return The configured MAC instance
     * @throws IOException If MAC creation fails
     */
    private Mac create()
        throws IOException {
        final String algo = String.format("HmacSHA%s", this.bits);
        try {
            final Mac mac = Mac.getInstance(algo);
            mac.init(new SecretKeySpec(this.key, algo));
            return mac;
        } catch (final NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new IOException(ex);
        }
    }
}
