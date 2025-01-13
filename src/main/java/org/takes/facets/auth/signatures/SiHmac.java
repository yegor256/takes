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
