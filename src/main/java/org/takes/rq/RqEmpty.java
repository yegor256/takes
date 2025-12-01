/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.util.Arrays;
import lombok.EqualsAndHashCode;
import org.cactoos.io.InputStreamOf;

/**
 * Empty HTTP request implementation for testing purposes.
 *
 * <p>This class creates minimal HTTP requests with only the request line
 * and no body content. It's primarily designed for unit testing scenarios
 * where a simple request structure is needed without additional headers
 * or body data.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.25
 */
@EqualsAndHashCode(callSuper = true)
public final class RqEmpty extends RqWrap {

    /**
     * Ctor.
     */
    public RqEmpty() {
        this("GET");
    }

    /**
     * Ctor.
     * @param method HTTP method
     */
    public RqEmpty(final CharSequence method) {
        this(method, "/ HTTP/1.1");
    }

    /**
     * Ctor.
     * @param method HTTP method
     * @param query HTTP query
     */
    public RqEmpty(final CharSequence method, final CharSequence query) {
        super(
            new RequestOf(
                Arrays.asList(String.format("%s %s", method, query)),
                new InputStreamOf("")
            )
        );
    }

}
