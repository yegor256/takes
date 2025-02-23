/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.InputStream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;

/**
 * Fluent response.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsFluent extends RsWrap {

    /**
     * Ctor.
     */
    public RsFluent() {
        this(new RsEmpty());
    }

    /**
     * Ctor.
     * @param res Original response
     */
    public RsFluent(final Response res) {
        super(res);
    }

    /**
     * With this status code.
     * @param code Status code
     * @return New fluent response
     */
    public RsFluent withStatus(final int code) {
        return new RsFluent(new RsWithStatus(this, code));
    }

    /**
     * With this header.
     * @param header The header
     * @return New fluent response
     */
    public RsFluent withHeader(final CharSequence header) {
        return new RsFluent(new RsWithHeader(this, header));
    }

    /**
     * With this header.
     * @param key Key
     * @param value Value
     * @return New fluent response
     */
    public RsFluent withHeader(final CharSequence key,
        final CharSequence value) {
        return new RsFluent(new RsWithHeader(this, key, value));
    }

    /**
     * With this content type.
     * @param ctype Content type
     * @return New fluent response
     */
    public RsFluent withType(final CharSequence ctype) {
        return new RsFluent(new RsWithType(this, ctype));
    }

    /**
     * With this body.
     * @param body Body
     * @return New fluent response
     */
    public RsFluent withBody(final CharSequence body) {
        return new RsFluent(new RsWithBody(this, body));
    }

    /**
     * With this body.
     * @param body Body
     * @return New fluent response
     */
    public RsFluent withBody(final byte[] body) {
        return new RsFluent(new RsWithBody(this, body));
    }

    /**
     * With this body.
     * @param body Body
     * @return New fluent response
     */
    public RsFluent withBody(final InputStream body) {
        return new RsFluent(new RsWithBody(this, body));
    }

}
