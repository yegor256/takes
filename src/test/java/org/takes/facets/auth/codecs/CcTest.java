/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.codecs;

import org.takes.facets.auth.Identity;

/**
 * Test codec.
 * @since 1.11.1
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
final class CcTest implements Codec {
    @Override
    public Identity decode(final byte[] bytes) {
        return new Identity.Simple(new String(bytes));
    }

    @Override
    public byte[] encode(final Identity identity) {
        return identity.urn().getBytes();
    }
}
