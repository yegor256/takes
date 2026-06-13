/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.hamcrest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.takes.Response;

/**
 * Response text body matcher.
 *
 * <p>This "matcher" tests given response body,
 * assuming that it has text content.</p>
 * <p>The class is immutable and thread-safe.
 *
 * @since 2.0
 */
public final class HmRsTextBody extends AbstractHmTextBody<Response> {

    /**
     * Ctor with equalTo matcher and default charset.
     * @param expected String to test against
     */
    public HmRsTextBody(final String expected) {
        this(new IsEqual<>(expected));
    }

    /**
     * Ctor with charset set to default one.
     * @param bdm Text body matcher
     */
    public HmRsTextBody(final Matcher<String> bdm) {
        this(bdm, StandardCharsets.UTF_8);
    }

    /**
     * Ctor.
     * @param bdm Text body matcher
     * @param charset Text body charset
     */
    public HmRsTextBody(final Matcher<String> bdm, final Charset charset) {
        super(bdm, charset);
    }

    @Override
    public InputStream itemBody(final Response item) throws IOException {
        return item.body();
    }
}
