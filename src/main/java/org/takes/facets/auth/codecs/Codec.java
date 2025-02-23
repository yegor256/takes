/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.IOException;
import org.takes.facets.auth.Identity;

/**
 * Codec.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 0.1
 */
public interface Codec {

    /**
     * Encode identity into bytes.
     * @param identity The identity
     * @return Text
     * @throws IOException If fails
     */
    byte[] encode(Identity identity) throws IOException;

    /**
     * Decode identity from text (or throw
     * {@link org.takes.facets.auth.codecs.DecodingException}).
     *
     * <p>This method may throw
     * {@link org.takes.facets.auth.codecs.DecodingException}, if it's not
     * possible to decode the incoming byte array. This exception will mean
     * that the user can't be authenticated and {@code Identity.ANONYMOUS}
     * object will be identified.
     *
     * @param bytes Text
     * @return Identity
     * @throws IOException If fails
     */
    Identity decode(byte[] bytes) throws IOException;

}
