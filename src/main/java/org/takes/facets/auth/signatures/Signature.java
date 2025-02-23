/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.signatures;

import java.io.IOException;

/**
 * Signature.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 1.4
 */
public interface Signature {

    /**
     * Create signature for data bytes.
     * @param data The data to be signed
     * @return Signature
     * @throws IOException If anything fails
     */
    byte[] sign(byte[] data) throws IOException;
}
