/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
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
 * AES codec that provides symmetric encryption using 128-bit keys with CBC mode.
 *
 * <p>This codec decorator encrypts identity data using the Advanced Encryption
 * Standard (AES) algorithm with Cipher Block Chaining (CBC) mode and PKCS5
 * padding. It generates a random initialization vector (IV) for each encryption
 * operation, ensuring that identical plaintexts produce different ciphertexts.
 *
 * <p>The encrypted format is: [16-byte IV][encrypted_data] where the IV is
 * prepended to allow for proper decryption. The key must be exactly 16 bytes
 * (128 bits) long.
 *
 * <p>It's recommended to use it in conjunction with {@link CcSigned} codec
 * for authentication, which can be applied
 * <a href="https://crypto.stackexchange.com/a/205">before or after</a>
 * encryption to provide both confidentiality and authenticity.
 *
 * <p>Usage example:
 * <pre> {@code
 * final String key = "1234567890123456"; // 16 bytes
 * final Codec codec = new CcAes(new CcPlain(), key);
 * final Identity identity = new Identity.Simple("urn:user:john", props);
 * final byte[] encrypted = codec.encode(identity);
 * final Identity decrypted = codec.decode(encrypted);
 * }</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.13.8
 */
@EqualsAndHashCode
public final class CcAes implements Codec {
    /**
     * Secure random instance.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

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
    private final Key key;

    /**
     * Random.
     */
    private final SecureRandom random;

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
        this(
            codec,
            CcAes.RANDOM,
            new SecretKeySpec(
                CcAes.withCorrectBlockSize(key.clone()), "AES"
            )
        );
    }

    /**
     * Constructor for the class.
     *
     * @param codec Original codec
     * @param random Random generator
     * @param key The encryption key
     */
    public CcAes(
        final Codec codec,
        final SecureRandom random,
        final Key key
    ) {
        this.origin = codec;
        this.key = key;
        this.random = random;
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
            final byte[] vector = new byte[CcAes.BLOCK];
            this.random.nextBytes(vector);
            final byte[] message = this.cipher(
                Cipher.ENCRYPT_MODE,
                new IvParameterSpec(vector)
            ).doFinal(bytes);
            final byte[] res = new byte[vector.length + message.length];
            System.arraycopy(vector, 0, res, 0, vector.length);
            System.arraycopy(
                message,
                0,
                res,
                vector.length,
                message.length
            );
            return res;
        } catch (final BadPaddingException | IllegalBlockSizeException ex) {
            throw new IOException(ex);
        }
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
        if (bytes.length < CcAes.BLOCK << 1) {
            throw new DecodingException("Invalid encrypted message format");
        }
        try {
            final byte[] vector = new byte[CcAes.BLOCK];
            final byte[] message = new byte[bytes.length - vector.length];
            System.arraycopy(bytes, 0, vector, 0, vector.length);
            System.arraycopy(
                bytes,
                vector.length,
                message,
                0,
                message.length
            );
            return this.cipher(
                Cipher.DECRYPT_MODE,
                new IvParameterSpec(vector)
            ).doFinal(message);
        } catch (final BadPaddingException | IllegalBlockSizeException ex) {
            throw new DecodingException(ex);
        }
    }

    /**
     * Create new cipher based on the valid mode from {@link Cipher} class.
     *
     * @param mode Either Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE
     * @param spec Param spec (IV)
     * @return The cipher
     * @throws IOException For any unexpected exceptions
     */
    private Cipher cipher(final int mode, final AlgorithmParameterSpec spec)
        throws IOException {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(mode, this.key, spec, this.random);
            return cipher;
        } catch (final InvalidKeyException | NoSuchAlgorithmException
            | InvalidAlgorithmParameterException
            | NoSuchPaddingException ex) {
            throw new IOException(ex);
        }
    }
}
