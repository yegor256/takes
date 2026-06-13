/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.InputStream;
import lombok.EqualsAndHashCode;
import org.cactoos.Scalar;
import org.cactoos.io.InputStreamOf;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.Sticky;
import org.cactoos.text.TextOf;
import org.takes.Request;

/**
 * Request decorator that prevents multiple reads of body content.
 *
 * <p>This decorator caches the request body on first access, ensuring that
 * subsequent calls to {@code body()} produce input streams over the same
 * cached content. The head is delegated to the original request without
 * caching. This is useful when the underlying body stream can only be read
 * once, but multiple components need to access it.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 2.0
 */
@EqualsAndHashCode(callSuper = true)
public final class BodyOnce extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     */
    public BodyOnce(final Request req) {
        super(
            new RequestOf(
                req::head,
                new IoChecked<>(
                    new Scalar<InputStream>() {
                        private final Scalar<String> text = new Sticky<>(
                            new TextOf(req::body)::asString
                        );

                        @Override
                        public InputStream value() throws Exception {
                            return new InputStreamOf(this.text.value());
                        }
                    }
                )::value
            )
        );
    }
}
