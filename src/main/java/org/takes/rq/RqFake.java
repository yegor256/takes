/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.cactoos.Text;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.io.InputStreamOf;

/**
 * Fake HTTP request implementation for testing purposes.
 *
 * <p>This class provides a convenient way to create mock HTTP requests
 * with custom headers and body content for unit testing. It supports
 * various constructor overloads to create requests with different
 * HTTP methods, query strings, headers, and body content.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class RqFake extends RqWrap {

    /**
     * Ctor.
     */
    public RqFake() {
        this("GET");
    }

    /**
     * Ctor.
     * @param method HTTP method
     */
    public RqFake(final CharSequence method) {
        this(method, "/ HTTP/1.1");
    }

    /**
     * Ctor.
     * @param method HTTP method
     * @param query HTTP query
     */
    public RqFake(final CharSequence method, final CharSequence query) {
        this(method, query, "");
    }

    /**
     * Ctor.
     * @param method HTTP method
     * @param query HTTP query
     * @param body HTTP body
     */
    public RqFake(final CharSequence method, final CharSequence query,
        final CharSequence body) {
        this(
            Arrays.asList(
                String.format("%s %s", method, query),
                "Host: www.example.com"
            ),
            body
        );
    }

    /**
     * Ctor.
     * @param head Head
     * @param body Body
     */
    public RqFake(final List<String> head, final CharSequence body) {
        this(
            head,
            new UncheckedBytes(
                new BytesOf(body.toString())
            ).asBytes());
    }

    /**
     * Ctor.
     * @param head Head
     * @param body Body
     */
    public RqFake(final List<String> head, final Text body) {
        this(
            head,
            new InputStreamOf(body)
        );
    }

    /**
     * Ctor.
     * @param head Head
     * @param body Body
     */
    public RqFake(final List<String> head, final byte[] body) {
        this(
            head,
            new ByteArrayInputStream(Arrays.copyOf(body, body.length))
        );
    }

    /**
     * Ctor.
     * @param head Head
     * @param body Body
     */
    public RqFake(final List<String> head, final InputStream body) {
        super(new RequestOf(Collections.unmodifiableList(head), body));
    }
}
