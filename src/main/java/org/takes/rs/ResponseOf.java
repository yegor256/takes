/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.cactoos.Scalar;
import org.cactoos.bytes.BytesOf;
import org.cactoos.scalar.And;
import org.cactoos.scalar.Equality;
import org.cactoos.scalar.HashCode;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.Or;
import org.cactoos.scalar.Unchecked;
import org.takes.Response;

/**
 * Response implementation that wraps custom head and body suppliers.
 *
 * <p>This class provides a flexible way to create Response instances using
 * functional interfaces (scalars) for head and body content. It accepts
 * suppliers that are called when the respective content is needed, enabling
 * lazy evaluation and custom response construction. This is particularly
 * useful for building responses dynamically.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 2.0
 */
public final class ResponseOf implements Response {

    /**
     * Original head scalar.
     */
    private final IoChecked<Iterable<String>> shead;

    /**
     * Original body scalar.
     */
    private final IoChecked<InputStream> sbody;

    /**
     * Ctor.
     * @param head Iterable head value
     * @param body InputStream body value
     */
    public ResponseOf(final Iterable<String> head, final InputStream body) {
        this(() -> head, () -> body);
    }

    /**
     * Ctor.
     * @param head Scalar to provide head value
     * @param body Scalar to provide body value
     */
    public ResponseOf(
        final Scalar<Iterable<String>> head, final Scalar<InputStream> body) {
        this.shead = new IoChecked<>(head);
        this.sbody = new IoChecked<>(body);
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.shead.value();
    }

    @Override
    public InputStream body() throws IOException {
        return this.sbody.value();
    }

    @Override
    @SuppressFBWarnings("EQ_UNUSUAL")
    public boolean equals(final Object that) {
        return new Unchecked<>(
            new Or(
                () -> this == that,
                new And(
                    () -> that != null,
                    () -> ResponseOf.class.equals(that.getClass()),
                    () -> {
                        final ResponseOf other = (ResponseOf) that;
                        return new And(
                            () -> {
                                final Iterator<String> iter = other.head()
                                    .iterator();
                                return new And(
                                    (String hdr) -> hdr.equals(iter.next()),
                                    this.head()
                                ).value();
                            },
                            () -> new Equality<>(
                                new BytesOf(this.body()),
                                new BytesOf(other.body())
                            ).value() == 0
                        ).value();
                    }
                )
            )
        ).value();
    }

    @Override
    public int hashCode() {
        return new HashCode(new Unchecked<>(this.shead).value()).value();
    }
}
