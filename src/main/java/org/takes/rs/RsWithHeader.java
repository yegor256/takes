/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.Scalar;
import org.cactoos.iterable.IterableOf;
import org.cactoos.iterable.Joined;
import org.takes.Response;

/**
 * Response decorator that adds a single header to an HTTP response.
 *
 * <p>This decorator appends one additional header to an existing response.
 * It validates header format according to RFC 7230 and provides multiple
 * constructor overloads for different header specification methods.
 * The decorator does not check for duplicate headers - it simply adds
 * the new header to the existing ones.
 *
 * <p><strong>Note:</strong> If you need to replace an existing header
 * rather than add a duplicate, combine this decorator with
 * {@link org.takes.rs.RsWithoutHeader}:
 * <pre>new RsWithHeader(
 *   new RsWithoutHeader(res, "Content-Type"),
 *   "Content-Type", "application/json"
 * )</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsWithHeader extends RsWrap {

    /**
     * Pattern for all other lines in the head.
     */
    private static final Pattern HEADER = Pattern.compile(
        "[a-zA-Z0-9\\-]+:\\p{Print}+"
    );

    /**
     * Ctor.
     * @param hdr Header
     * @since 0.8
     */
    public RsWithHeader(final CharSequence hdr) {
        this(new RsEmpty(), hdr);
    }

    /**
     * Ctor.
     * @param name Header name
     * @param value Header value
     * @since 0.8
     */
    public RsWithHeader(final CharSequence name, final CharSequence value) {
        this(new RsEmpty(), name, value);
    }

    /**
     * Ctor.
     * @param res Original response
     * @param name Header name
     * @param value Header value
     */
    public RsWithHeader(final Response res, final CharSequence name,
        final CharSequence value) {
        this(res, String.format("%s: %s", name, value));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param header Header to add
     */
    public RsWithHeader(final Response res, final CharSequence header) {
        this(res, () -> header);
    }

    /**
     * Ctor.
     * @param res Original response
     * @param header Header to add
     */
    public RsWithHeader(final Response res, final Scalar<CharSequence> header) {
        super(
            new ResponseOf(
                () -> RsWithHeader.extend(res.head(), header.value().toString()),
                res::body
            )
        );
    }

    /**
     * Add to head additional header.
     * @param head Original head
     * @param header Value witch will be added to head
     * @return Head with additional header
     */
    private static Iterable<String> extend(final Iterable<String> head,
        final String header) {
        if (!RsWithHeader.HEADER.matcher(header).matches()) {
            throw new IllegalArgumentException(
                String.format(
                    "header line of HTTP response \"%s\" doesn't match \"%s\" regular expression, but it should, according to RFC 7230",
                    header, RsWithHeader.HEADER
                )
            );
        }
        return new Joined<String>(head, new IterableOf<>(header));
    }

}
