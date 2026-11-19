/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import lombok.EqualsAndHashCode;
import org.cactoos.scalar.Sticky;
import org.takes.Response;
import org.takes.rs.ResponseOf;
import org.takes.rs.RsWrap;

/**
 * Response whose head is computed only once.
 *
 * <p>The back-end reads the head of a response twice: once to decide
 * whether the connection stays open, and once to print it. A head built
 * lazily by a take could change between the two reads, or cost twice.
 * This response computes the head on the first read and returns the same
 * lines afterwards, mirroring {@link org.takes.rq.HeadOnce} for
 * requests.</p>
 *
 * @since 1.20
 */
@EqualsAndHashCode(callSuper = true)
final class RsHeadOnce extends RsWrap {

    /**
     * Ctor.
     *
     * @param res Original response
     */
    RsHeadOnce(final Response res) {
        super(new ResponseOf(new Sticky<>(res::head), res::body));
    }
}
