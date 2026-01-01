/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.IOException;
import org.takes.facets.auth.Identity;

/**
 * Codec interface for encoding and decoding identity objects to and from byte arrays.
 *
 * <p>This interface defines the contract for transforming Identity objects
 * into byte representations and vice versa. Implementations can provide
 * various encoding formats (plain text, binary, encrypted, compressed, etc.)
 * and can be composed using the decorator pattern for layered functionality.
 *
 * <p>The interface supports throwing IOException for encoding operations
 * and DecodingException (which is a RuntimeException) for decoding failures.
 * This allows codecs to handle network issues, encryption failures, and
 * malformed data appropriately.
 *
 * <p>Usage example:
 * <pre> {@code
 * final Codec codec = new CcBase64(new CcGzip(new CcPlain()));
 * final Identity identity = new Identity.Simple("urn:user:john", props);
 * final byte[] encoded = codec.encode(identity);
 * final Identity decoded = codec.decode(encoded);
 * }</pre>
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
