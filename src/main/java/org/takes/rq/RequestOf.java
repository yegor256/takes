/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.cactoos.bytes.BytesOf;
import org.cactoos.scalar.And;
import org.cactoos.scalar.Equality;
import org.cactoos.scalar.HashCode;
import org.cactoos.scalar.Or;
import org.cactoos.scalar.Unchecked;
import org.takes.Body;
import org.takes.Head;
import org.takes.Request;

/**
 * Request implementation that wraps custom head and body suppliers.
 *
 * <p>This class provides a flexible way to create Request instances using
 * functional interfaces for head and body content. It accepts suppliers
 * (Head and Body interfaces) that are called when the respective content
 * is needed, enabling lazy evaluation and custom request construction.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 2.0
 */
public final class RequestOf implements Request {
    /**
     * Original head scalar.
     */
    private final Head shead;

    /**
     * Original body scalar.
     */
    private final Body sbody;

    /**
     * Ctor.
     * @param head Iterable head value
     * @param body InputStream body value
     */
    public RequestOf(final Iterable<String> head, final InputStream body) {
        this(() -> head, () -> body);
    }

    /**
     * Ctor.
     * @param head Head value
     * @param body Body value
     */
    public RequestOf(final Head head, final Body body) {
        this.shead = head;
        this.sbody = body;
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.shead.head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.sbody.body();
    }

    @Override
    @SuppressFBWarnings("EQ_UNUSUAL")
    public boolean equals(final Object that) {
        return new Unchecked<>(
            new Or(
                () -> this == that,
                new And(
                    () -> that != null,
                    () -> RequestOf.class.equals(that.getClass()),
                    () -> {
                        final RequestOf other = (RequestOf) that;
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
        return new HashCode(new Unchecked<>(this.shead::head).value()).value();
    }
}
