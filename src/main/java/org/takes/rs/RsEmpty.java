/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
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
 * Empty response.
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
