/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 Yegor Bugayenko
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
package org.takes.facets.auth.codecs;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.EqualsAndHashCode;
import org.takes.facets.auth.Identity;

/**
 * AES codec which supports 128 bits key.
 *
 * <p>The class is immutable and thread-safe.
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @since 0.13.8
 */
@EqualsAndHashCode
public final class CcAes implements Codec {
    /**
     * The block size constant.
     */
    private static final int BLOCK = 16;

    /**
     * Original codec.
     */
    private final Codec origin;

    /**
     * The encryption key.
     */
    private final byte[] key;

    /**
     * The algorithm parameter spec for cipher.
     */
    private final AlgorithmParameterSpec spec;

    /**
     * Constructor for the class.
     *
     * @param codec Original codec
     * @param key The encryption key
     * @since 0.22
     */
    public CcAes(final Codec codec, final String key) {
        this(codec, key.getBytes(Charset.defaultCharset()));
    }

    /**
     * Constructor for the class.
     *
     * @param codec Original codec
     * @param key The encryption key
     */
    public CcAes(final Codec codec, final byte[] key) {
        this.origin = codec;
        this.key = key.clone();
        this.spec = CcAes.algorithmParameterSpec();
    }

    @Override
    public byte[] encode(final Identity identity) throws IOException {
        return this.encrypt(this.origin.encode(identity));
    }

    @Override
    public Identity decode(final byte[] bytes) throws IOException {
        return this.origin.decode(this.decrypt(bytes));
    }

    /**
     * Encrypt the given bytes using AES.
     *
     * @param bytes Bytes to encrypt
     * @return Encrypted byte using AES algorithm
     * @throws IOException for all unexpected exceptions
     */
    private byte[] encrypt(final byte[] bytes) throws IOException {
        try {
            return this.create(Cipher.ENCRYPT_MODE).doFinal(bytes);
        } catch (final BadPaddingException ex) {
            throw new IOException(ex);
        } catch (final IllegalBlockSizeException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Create AlgorithmParameterSpec with the block size.
     *
     * @return The AlgorithmParameterSpec
     */
    private static AlgorithmParameterSpec algorithmParameterSpec() {
        final SecureRandom random = new SecureRandom();
        final byte[] bytes = new byte[CcAes.BLOCK];
        random.nextBytes(bytes);
        return new IvParameterSpec(bytes);
    }

    /**
     * Check the block size of the key.
     *
     * @param key The encryption key
     * @return The verified encryption key
     */
    private static byte[] withCorrectBlockSize(final byte[] key) {
        if (key.length != CcAes.BLOCK) {
            throw new IllegalArgumentException(
                String.format(
                    "the length of the AES key must be exactly %d bytes",
                    CcAes.BLOCK
                )
            );
        }
        return key;
    }

    /**
     * Decrypt the given bytes using AES.
     *
     * @param bytes Bytes to decrypt
     * @return Decrypted bytes
     * @throws IOException for all unexpected exceptions
     */
    private byte[] decrypt(final byte[] bytes) throws IOException {
        try {
            return this.create(Cipher.DECRYPT_MODE).doFinal(bytes);
        } catch (final BadPaddingException ex) {
            throw new IOException(ex);
        } catch (final IllegalBlockSizeException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Create new cipher based on the valid mode from {@link Cipher} class.
     *
     * @param mode Either Cipher.ENRYPT_MODE or Cipher.DECRYPT_MODE
     * @return The cipher
     * @throws IOException For any unexpected exceptions
     */
    private Cipher create(final int mode)
        throws IOException {
        try {
            final SecretKeySpec secret = new SecretKeySpec(
                CcAes.withCorrectBlockSize(this.key), "AES"
            );
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(mode, secret, this.spec);
            return cipher;
        } catch (final InvalidKeyException ex) {
            throw new IOException(ex);
        } catch (final NoSuchAlgorithmException ex) {
            throw new IOException(ex);
        } catch (final NoSuchPaddingException ex) {
            throw new IOException(ex);
        } catch (final InvalidAlgorithmParameterException ex) {
            throw new IOException(ex);
        }
    }
}
