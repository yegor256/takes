/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
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
 * Request decorator, to prevent multiple calls to {@code body()} method.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.36
 * @todo #1080:30min Please make two decorators (HeadOnce and BodyOnce)
 *  that prevent multiple reads of their contents. Introduce some units
 *  tests for these new classes.
 */
@EqualsAndHashCode(callSuper = true)
public final class RqOnce extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     */
    public RqOnce(final Request req) {
        super(
            new RequestOf(
                new IoChecked<>(new Sticky<>(req::head))::value,
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
