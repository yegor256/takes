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
package org.takes.facets.auth.codecs;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.EqualsAndHashCode;

import org.takes.facets.auth.Identity;

/**
 * AES codec which support 128 bits key.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @since 0.13.8
 */
@EqualsAndHashCode(of = { "origin", "secret", "ivbytes" })
public final class CcAES implements Codec {

    /**
     * The algorithm used.
     */
    private static final transient String ALGORITHM = "AES/CBC/PKCS5PADDING";

    /**
     * Default block size (128 bits) in bytes.
     */
    private static final transient int BLOCK = 16;

    /**
     * Original codec.
     */
    private final transient Codec origin;

    /**
     * Secret to use for encoding.
     */
    private final transient byte[] secret;

    /**
     * Initialization Vector.
     */
    private final transient byte[] ivbytes;

    /**
     * The AES secret key object.
     */
    private final SecretKey passcode;

    /**
     * Ctor.
     * @param codec Original codec
     * @param key The encryption key
     */
    public CcAES(final Codec codec, final byte[] key) {
        if (key.length != BLOCK) {
            throw new IllegalArgumentException(
                    "Key length must be in 128-bit."
            );
        }
        this.origin = codec;
        this.secret = new byte[key.length];
        System.arraycopy(key, 0, this.secret, 0, key.length);
        final SecureRandom random = new SecureRandom();
        this.ivbytes = new byte[BLOCK];
        random.nextBytes(this.ivbytes);
        this.passcode = new SecretKeySpec(this.secret, "AES");
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
     * Encrypt the given bytes using AES
     *
     * @param bytes Bytes to encrypt
     * @return Encrypted byte using AES algorithm
     * @throws IOException for all unexpected exceptions
     */
    private byte[] encrypt(final byte[] bytes) throws IOException {
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            final AlgorithmParameterSpec spec = new IvParameterSpec(this.ivbytes);
            cipher.init(Cipher.ENCRYPT_MODE, this.passcode, spec);
            return cipher.doFinal(bytes);
        } catch (InvalidKeyException ike) {
            throw new IOException(ike);
        } catch (NoSuchAlgorithmException nse) {
            throw new IOException(nse);
        } catch (NoSuchPaddingException nspe) {
            throw new IOException(nspe);
        } catch (InvalidAlgorithmParameterException iape) {
            throw new IOException(iape);
        } catch (IllegalBlockSizeException ibse) {
            throw new IOException(ibse);
        } catch (BadPaddingException bpe) {
            throw new IOException(bpe);
        }
    }

    /**
     * Decrypt the given bytes using AES
     *
     * @param bytes Bytes to decrypt
     * @return Decrypted bytes
     * @throws IOException for all unexpected exceptions
     */
    private byte[] decrypt(final byte[] bytes) throws IOException {
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            final AlgorithmParameterSpec spec = new IvParameterSpec(this.ivbytes);
            cipher.init(Cipher.DECRYPT_MODE, this.passcode, spec);
            return cipher.doFinal(bytes);
        } catch (InvalidKeyException ike) {
            throw new IOException(ike);
        } catch (NoSuchAlgorithmException nse) {
            throw new IOException(nse);
        } catch (NoSuchPaddingException nspe) {
            throw new IOException(nspe);
        } catch (InvalidAlgorithmParameterException iape) {
            throw new IOException(iape);
        } catch (IllegalBlockSizeException ibse) {
            throw new IOException(ibse);
        } catch (BadPaddingException bpe) {
            throw new IOException(bpe);
        }
    }

}
