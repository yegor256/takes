/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.util.Arrays;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.facets.auth.Identity;

/**
 * Test case for {@link CcAes}.
 * @since 0.13.8
 */
final class CcAesTest {
    @Test
    void encryptIdentity() throws Exception {
        final byte[] key = {
            (byte) -25, (byte) 62, (byte) 118, (byte) 92,
            (byte) -35, (byte) -24, (byte) 92, (byte) 48,
            (byte) 5, (byte) -4, (byte) -88, (byte) -95,
            (byte) -110, (byte) -54, (byte) 43, (byte) -1,
        };
        final byte[] random = {
            (byte) 63, (byte) -27, (byte) -43, (byte) -52,
            (byte) -70, (byte) -44, (byte) 86, (byte) -43,
            (byte) -43, (byte) 116, (byte) -122, (byte) 105,
            (byte) 108, (byte) -25, (byte) -126, (byte) 90,
        };
        final byte[] encrypted = new CcAes(
            new CcTest(),
            new CcAesTest.FkRandom(random),
            new SecretKeySpec(key, "AES")
        ).encode(new Identity.Simple("urg:github:0000"));
        MatcherAssert.assertThat(
            "Encrypted identity does not start with IV",
            Arrays.copyOf(encrypted, 16),
            Matchers.equalTo(random)
        );
        final byte[] message = new byte[encrypted.length - 16];
        System.arraycopy(encrypted, 16, message, 0, message.length);
        MatcherAssert.assertThat(
            "Encrypted message did not match",
            message,
            Matchers.equalTo(
                new byte[]{
                    (byte) -119, (byte) -114, (byte) 19, (byte) 21,
                    (byte) 77, (byte) 59, (byte) 22, (byte) 100,
                    (byte) 121, (byte) -116, (byte) -43, (byte) 24,
                    (byte) 86, (byte) 24, (byte) -42, (byte) 119,
                }
            )
        );
    }

    @Test
    void decryptIdentity() throws Exception {
        final byte[] encrypted = {
            (byte) 83, (byte) -12, (byte) -8, (byte) 30,
            (byte) -24, (byte) -5, (byte) -72, (byte) -33,
            (byte) 13, (byte) 57, (byte) -37, (byte) -47,
            (byte) -95, (byte) 108, (byte) 43, (byte) 101,
            (byte) -87, (byte) -108, (byte) -41, (byte) 0,
            (byte) 97, (byte) 1, (byte) -120, (byte) -39,
            (byte) -114, (byte) 80, (byte) 18, (byte) -76,
            (byte) -12, (byte) 10, (byte) -50, (byte) 51,
            (byte) 66, (byte) 11, (byte) 13, (byte) -115,
            (byte) 17, (byte) -41, (byte) -84, (byte) -78,
            (byte) 48, (byte) 47, (byte) 42, (byte) -92,
            (byte) -127, (byte) 16, (byte) -74, (byte) -61,
        };
        final byte[] key = {
            (byte) 25, (byte) 92, (byte) 9, (byte) -75,
            (byte) 54, (byte) 20, (byte) -118, (byte) 73,
            (byte) -2, (byte) 81, (byte) 24, (byte) -5,
            (byte) 20, (byte) 122, (byte) 92, (byte) -128,
        };
        MatcherAssert.assertThat(
            new CcAes(
                new CcTest(),
                new SecureRandom(),
                new SecretKeySpec(key, "AES")
            ).decode(encrypted).urn(),
            Matchers.equalTo("urn:github:29835")
        );
    }

    @Test
    void encodesAndDecodes() throws Exception {
        final int length = 128;
        final KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(length);
        final byte[] key = generator.generateKey().getEncoded();
        final String plain = "This is a test!!@@**";
        final Codec codec = new CcAes(new CcTest(), key);
        MatcherAssert.assertThat(
            codec.decode(codec.encode(new Identity.Simple(plain))).urn(),
            Matchers.equalTo(plain)
        );
    }

    @Test
    void throwsRightWhenBroken() {
        Assertions.assertThrows(
            DecodingException.class,
            () -> new CcAes(
                new CcPlain(), "0123456701234567"
            ).decode("broken input".getBytes())
        );
    }

    /**
     * Fake random with provided random result.
     * @since 0.13.8
     */
    private static final class FkRandom extends SecureRandom {
        /**
         * Serial id.
         */
        private static final long serialVersionUID = 8646596235826414879L;

        /**
         * Ctor.
         * @param fake Bytes
         */
        FkRandom(final byte[] fake) {
            super(new CcAesTest.FkRandomSpi(fake), null);
        }
    }

    /**
     * Fake random SPI.
     * @since 0.13.8
     */
    @SuppressWarnings("PMD.UncommentedEmptyMethodBody")
    private static final class FkRandomSpi extends SecureRandomSpi {

        /**
         * Serial id.
         */
        private static final long serialVersionUID = -5153681125995322457L;

        /**
         * Bytes.
         */
        private final byte[] fake;

        /**
         * Ctor.
         * @param fake Bytes
         */
        FkRandomSpi(final byte[] fake) {
            super();
            this.fake = fake.clone();
        }

        @Override
        public void engineSetSeed(final byte[] bytes) {
        }

        @Override
        public void engineNextBytes(final byte[] bytes) {
            if (bytes.length > this.fake.length) {
                throw new UnsupportedOperationException(
                    "Byte-array is too big"
                );
            }
            System.arraycopy(this.fake, 0, bytes, 0, bytes.length);
        }

        @Override
        public byte[] engineGenerateSeed(final int length) {
            return new byte[length];
        }
    }
}
