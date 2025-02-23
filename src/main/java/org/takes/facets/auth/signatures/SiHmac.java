/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.signatures;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.EqualsAndHashCode;

/**
 * HMAC codec which supports 256, 384 and 512 bit hash.
 *
 * <p>
 * The class is immutable and thread-safe.
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
     * Ctor.
     *
     * @param key
     *  The encryption key
     */
    public SiHmac(final String key) {
        this(key.getBytes(Charset.defaultCharset()), SiHmac.HMAC256);
    }

    /**
     * Ctor.
     *
     * @param key
     *  The encryption key
     * @param bits
     *  The signature bit length
     */
    public SiHmac(final String key, final int bits) {
        this(key.getBytes(Charset.defaultCharset()), bits);
    }

    /**
     * Ctor.
     *
     * @param key
     *  The encryption key
     * @param bits
     *  The signature bit length
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
     * Signature bit length.
     *
     * @return The bitlength
     */
    public int bitlength() {
        return this.bits;
    }

    /**
     * Check for correct bit length.
     *
     * @param bits
     *  Given bit length
     * @return Original bit length when appropriate, 256 bits otherwise.
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
     * Encrypt the given bytes using HMAC.
     *
     * @param bytes
     *  Bytes to encrypt
     * @return Encrypted bytes
     * @throws IOException
     *  for all unexpected exceptions
     */
    private byte[] encrypt(final byte[] bytes) throws IOException {
        try (Formatter formatter = new Formatter()) {
            for (final byte byt : this.create().doFinal(bytes)) {
                formatter.format("%02x", byt);
            }
            return formatter.toString().getBytes(Charset.defaultCharset());
        }
    }

    /**
     * Create new mac based on a valid bit length from {@link Mac} class.
     *
     * @return The mac
     * @throws IOException
     *  For any unexpected exceptions
     */
    private Mac create()
        throws IOException {
        final String algo = String.format("HmacSHA%s", this.bits);
        try {
            final SecretKeySpec secret = new SecretKeySpec(
                this.key, algo
            );
            final Mac mac = Mac.getInstance(algo);
            mac.init(secret);
            return mac;
        } catch (final NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new IOException(ex);
        }
    }
}
