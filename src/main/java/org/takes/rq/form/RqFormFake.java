/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.form;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import org.takes.Request;
import org.takes.rq.RqForm;
import org.takes.rq.RqWithBody;

/**
 * RqFormFake accepts parameters in the constructor.
 * @since 0.33
 */
public final class RqFormFake implements RqForm {

    /**
     * RqFormFake form request.
     */
    private final RqForm fake;

    /**
     * Ctor.
     * @param req Original request
     * @param params Parameters
     */
    public RqFormFake(final Request req, final String... params) {
        this.fake = new RqFormBase(
            new RqWithBody(req, construct(validated(params)))
        );
    }

    @Override
    public Iterable<String> param(final CharSequence name)
        throws IOException {
        return this.fake.param(name);
    }

    @Override
    public Iterable<String> names() throws IOException {
        return this.fake.names();
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.fake.head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.fake.body();
    }

    /**
     * Validate parameters.
     * @param params Parameters
     * @return Validated parameters if their count is even.
     * @throws IllegalArgumentException if parameters count is odd.
     */
    private static String[] validated(final String... params) {
        if (params.length % 2 != 0) {
            throw new IllegalArgumentException(
                "Wrong number of parameters"
            );
        }
        return params;
    }

    /**
     * Construct request body from parameters.
     * @param params Parameters
     * @return Request body
     */
    private static String construct(final String... params) {
        final StringBuilder builder = new StringBuilder();
        for (int idx = 0; idx < params.length; idx += 2) {
            builder.append(encode(params[idx]))
                .append('=')
                .append(encode(params[idx + 1]))
                .append('&');
        }
        return builder.toString();
    }

    /**
     * Encode text.
     * @param txt Text
     * @return Encoded text
     */
    private static String encode(final CharSequence txt) {
        try {
            return URLEncoder.encode(
                txt.toString(), Charset.defaultCharset().name()
            );
        } catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException(
                String.format("Failed to encode '%s'", txt),
                ex
            );
        }
    }
}
