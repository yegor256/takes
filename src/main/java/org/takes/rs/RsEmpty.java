/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.list.ListOf;
import org.takes.Response;

/**
 * Empty HTTP response with 204 No Content status.
 *
 * <p>This response implementation returns an HTTP 204 status code
 * with an empty body. It's typically used for successful operations
 * that don't need to return any content to the client, such as
 * DELETE operations or successful form submissions.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
public final class RsEmpty implements Response {

    @Override
    public Iterable<String> head() {
        return new ListOf<>("HTTP/1.1 204 No Content");
    }

    @Override
    public InputStream body() {
        return new ByteArrayInputStream(new byte[0]);
    }
}
