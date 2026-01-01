/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.hamcrest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.takes.Request;

/**
 * Request text body matcher.
 *
 * <p>This "matcher" tests given request body,
 * assuming that it has text content.</p>
 * <p>The class is immutable and thread-safe.
 *
 * @since 2.0
 */
public final class HmRqTextBody extends AbstractHmTextBody<Request> {

    /**
     * Ctor with equalTo matcher and default charset.
     * @param expected String to test against
     */
    public HmRqTextBody(final String expected) {
        this(Matchers.equalTo(expected));
    }

    /**
     * Ctor with charset set to default one.
     * @param bdm Text body matcher
     */
    public HmRqTextBody(final Matcher<String> bdm) {
        this(bdm, Charset.defaultCharset());
    }

    /**
     * Ctor.
     * @param bdm Text body matcher
     * @param charset Text body charset
     */
    public HmRqTextBody(final Matcher<String> bdm, final Charset charset) {
        super(bdm, charset);
    }

    @Override
    public InputStream itemBody(final Request item) throws IOException {
        return item.body();
    }
}
