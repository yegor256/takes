/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
import java.util.regex.Pattern;
import org.takes.facets.auth.Identity;

/**
 * Decorator which check incoming and outgoing identities.
 *
 * <p>The class is immutable and thread-safe.
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
