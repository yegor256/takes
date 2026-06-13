/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import java.io.IOException;
import java.util.regex.Pattern;
import org.takes.facets.auth.Identity;

/**
 * Decorator that validates incoming and outgoing identities according to
 * strict URN format rules.
 *
 * <p>This codec decorator validates that identity URNs conform to the
 * RFC 8141 URN specification. It checks both during encoding and decoding
 * to ensure that only valid URNs are processed. Anonymous identities
 * are allowed to pass through without validation.
 *
 * <p>The validation ensures URNs follow the format:
 * {@code urn:namespace-id:namespace-specific-string[?query][#fragment]}
 * where each component follows the specified character restrictions.
 *
 * <p>Usage example:
 * <pre> {@code
 * final Codec codec = new CcStrict(new CcPlain());
 * final Identity identity = new Identity.Simple("urn:user:john", props);
 * final byte[] encoded = codec.encode(identity); // validates URN
 * final Identity decoded = codec.decode(encoded); // validates URN
 * }</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.11.2
 */
public final class CcStrict implements Codec {

    /**
     * URN matching pattern.
     */
    private static final Pattern PTN = Pattern.compile(
        "^(?i)^urn(?-i):[a-zA-Z0-9]([\\-a-zA-Z0-9]{1,31})(:([\\-a-zA-Z0-9/]|%[0-9a-fA-F]{2})*)+(\\?\\w+(=([\\-a-zA-Z0-9/]|%[0-9a-fA-F]{2})*)?(&\\w+(=([\\-a-zA-Z0-9/]|%[0-9a-fA-F]{2})*)?)*)?\\*?$"
    );

    /**
     * Original codec.
     */
    private final Codec origin;

    /**
     * Ctor.
     * @param codec Original codec
     */
    public CcStrict(final Codec codec) {
        this.origin = codec;
    }

    @Override
    public byte[] encode(final Identity identity) throws IOException {
        return this.origin.encode(CcStrict.applyRules(identity));
    }

    @Override
    public Identity decode(final byte[] bytes) throws IOException {
        return CcStrict.applyRules(this.origin.decode(bytes));
    }

    /**
     * Apply validation rules to identity.
     * @param identity Identity
     * @return Identity
     */
    private static Identity applyRules(final Identity identity) {
        if (!identity.equals(Identity.ANONYMOUS)) {
            final String urn = identity.urn();
            if (urn.isEmpty()) {
                throw new DecodingException("urn is empty");
            }
            if (!CcStrict.PTN.matcher(urn).matches()) {
                throw new DecodingException(
                    String.format("urn isn't valid: \"%s\"", urn)
                );
            }
        }
        return identity;
    }
}
