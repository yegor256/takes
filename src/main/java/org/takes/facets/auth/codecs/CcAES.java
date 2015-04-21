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
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.EqualsAndHashCode;
import org.takes.facets.auth.Identity;
import com.jcabi.aspects.Immutable;

/**
 * AES codec which supports 128 bits key.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @since 0.13.8
 */
@Immutable
@EqualsAndHashCode(of = { "origin", "secret", "ivbytes" })
public final class CcAES implements Codec {

    /**
     * The algorithm used.
     */
    private static final transient String ALGORITHM = "AES/CBC/PKCS5PADDING";

    /**
     * The cipher for encryption.
     */
    private final transient Cipher enc;

    /**
     * The cipher for decryption. 
     */
    private final transient Cipher dec;

    /**
     * Original codec.
     */
    private final transient Codec origin;

    /**
     * Initialization Vector.
     */
    private final transient byte[] ivbytes;

    /**
     * The AES secret key object.
     */
    private final transient SecretKey secret;

    /**
     * Ctor.
     * @param codec Original codec
     * @param key The encryption key
     * @exception IOException exception throw when there are errors on 
     * creating internal components
     */
    public CcAES(final Codec codec, final byte[] key) throws IOException {
        final int block = 16;
        if (key.length != block) {
            throw new IllegalArgumentException(
                    "Key length must be in 128-bit."
            );
        }
        this.origin = codec;
        final byte[] passcode = new byte[key.length];
        System.arraycopy(key, 0, passcode, 0, key.length);
        final SecureRandom random = new SecureRandom();
        this.ivbytes = new byte[block];
        random.nextBytes(this.ivbytes);
        final AlgorithmParameterSpec spec =
                new IvParameterSpec(this.ivbytes);
        this.secret = new SecretKeySpec(passcode, "AES");
        this.enc = this.create(Cipher.ENCRYPT_MODE, spec);
        this.dec = this.create(Cipher.DECRYPT_MODE, spec);
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
            return this.enc.doFinal(bytes);
        } catch (final GeneralSecurityException gse) {
            throw new IOException(gse);
        }
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
            return this.dec.doFinal(bytes);
        } catch (final GeneralSecurityException gse) {
            throw new IOException(gse);
        }
    }

    /**
     * Create new cipher based on the valid mode from {@link Cipher} class.
     * @param mode The cipher mode, either Cipher.ENRYPT_MODE or Cipher.DECRYPT_MODE
     * @return The cipher
     * @throws IOException For any unexpected exceptions
     */
    private Cipher create(final int mode, final AlgorithmParameterSpec spec) throws IOException {
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(mode, this.secret, spec);
            return cipher;
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse);
        }
    }

}
