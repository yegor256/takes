/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.nio.charset.Charset;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Response decorator that sets the Content-Type header.
 *
 * <p>This decorator adds or replaces the Content-Type header in an HTTP
 * response with the specified media type and optional charset parameter.
 * It removes any existing Content-Type headers to avoid duplication.
 * The class also provides convenient inner classes for common content
 * types like HTML, JSON, XML, and plain text.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsWithType extends RsWrap {

    /**
     * Type header.
     */
    private static final String HEADER = "Content-Type";

    /**
     * The name of the parameter allowing to define the character set.
     */
    private static final String CHARSET = "charset";

    /**
     * Constructs a {@code RsWithType} that will add the content type header to
     * the response using the specified type as media type.
     *
     * <p>The resulting header is of type {@code Content-Type: media-type}.
     *
     * @param res Original response
     * @param type Content type
     */
    public RsWithType(final Response res, final CharSequence type) {
        this(res, type, new Opt.Empty<>());
    }

    /**
     * Constructs a {@code RsWithType} that will add the content type header to
     * the response using the specified type as media type and the specified
     * charset as charset parameter value.
     *
     * <p>The resulting header
     * is of type {@code Content-Type: media-type; charset=charset-value}.
     *
     * @param res Original response
     * @param type Content type
     * @param charset The character set to add in the content type header
     */
    public RsWithType(final Response res, final CharSequence type,
        final Charset charset) {
        this(res, type, new Opt.Single<>(charset));
    }

    /**
     * Constructs a {@code RsWithType} that will add the content type header to
     * the response using the specified type as media type and the specified
     * charset as charset parameter value if present.
     * @param res Original response
     * @param type Content type
     * @param charset The character set to add in the content type header if
     *  present
     */
    private RsWithType(final Response res, final CharSequence type,
        final Opt<Charset> charset) {
        super(new LazyRs(res, type, charset));
    }

    static Response make(final Response res, final CharSequence type,
        final Opt<Charset> charset) {
        final Response response;
        if (charset.has()) {
            response = new RsWithHeader(
                new RsWithoutHeader(res, RsWithType.HEADER),
                RsWithType.HEADER,
                new UncheckedText(
                    new FormattedText(
                        "%s; %s=%s",
                        type,
                        RsWithType.CHARSET,
                        charset.get().name()
                    )
                ).asString()
            );
        } else {
            response = new RsWithHeader(
                new RsWithoutHeader(res, RsWithType.HEADER),
                RsWithType.HEADER,
                type
            );
        }
        return response;
    }
}
