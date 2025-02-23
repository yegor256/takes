/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
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
 * Response decorator, with an additional header.
 *
 * <p>Remember, if a header is already present in the response, this
 * decorator will add another one, with the same name. It doesn't check
 * for duplicates. If you want to avoid duplicate headers, use this
 * decorator in combination with {@link org.takes.rs.RsWithoutHeader},
 * for example:
 *
 * <pre> new RsWithHeader(
 *   new RsWithoutHeader(res, "Host"),
 *   "Host", "www.example.com"
 * )</pre>
 *
 * <p>In this example, {@link org.takes.rs.RsWithoutHeader} will remove the
 * {@code Host} header first and {@link org.takes.rs.RsWithHeader} will
 * add a new one.
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
