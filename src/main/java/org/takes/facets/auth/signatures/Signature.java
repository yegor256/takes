/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.signatures;

import java.io.IOException;

/**
 * A signature interface for creating cryptographic signatures from data bytes.
 *
 * <p>This interface defines the contract for signature implementations that can
 * create cryptographic signatures from byte arrays. All implementations of this
 * interface must be immutable and thread-safe.
 *
 * @since 1.4
 */
public interface Signature {

    /**
     * Creates a signature for the given data bytes.
     * @param data The data to be signed
     * @return The signature as a byte array
     * @throws IOException If signature creation fails
     */
    byte[] sign(byte[] data) throws IOException;
}
