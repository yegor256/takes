/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import java.nio.charset.Charset;
import lombok.EqualsAndHashCode;
import lombok.ToString;
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
     * <p>The resulting header is of type {@code Content-Type: media-type}.
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
     * <p>The resulting header
     * is of type {@code Content-Type: media-type; charset=charset-value}.
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
     *  present.
     */
    private RsWithType(final Response res, final CharSequence type,
        final Opt<Charset> charset) {
        super(RsWithType.make(res, type, charset));
    }

    /**
     * Factory allowing to create {@code Response} with the corresponding
     * content type and character set.
     * @param res Original response
     * @param type Content type
     * @param charset The character set to add to the content type header. If
     *  absent no character set will be added to the content type header
     * @return Response
     */
    private static Response make(final Response res, final CharSequence type,
        final Opt<Charset> charset) {
        final Response response;
        if (charset.has()) {
            response = new RsWithHeader(
                new RsWithoutHeader(res, RsWithType.HEADER),
                RsWithType.HEADER,
                String.format(
                    "%s; %s=%s",
                    type,
                    RsWithType.CHARSET,
                    charset.get().name()
                )
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

    /**
     * Response decorator, with content type text/html.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @since 0.30
     */
    public static final class Html extends RsWrap {

        /**
         * Constructs a {@code HTML} that will add text/html as the content type
         * header to the response.
         * @param res Original response
         */
        public Html(final Response res) {
            this(res, new Opt.Empty<>());
        }

        /**
         * Constructs a {@code HTML} that will add text/html as the content type
         * header to the response using the specified charset as charset
         * parameter value.
         * @param res Original response
         * @param charset The character set to add in the content type header
         */
        public Html(final Response res, final Charset charset) {
            this(res, new Opt.Single<>(charset));
        }

        /**
         * Constructs a {@code HTML} that will add text/html as the content type
         * header to the response using the specified charset as charset
         * parameter value if present.
         * @param res Original response
         * @param charset The character set to add in the content type header if
         *  present.
         */
        private Html(final Response res, final Opt<Charset> charset) {
            super(RsWithType.make(res, "text/html", charset));
        }

    }

    /**
     * Response decorator, with content type application/json.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @since 0.30
     */
    public static final class Json extends RsWrap {

        /**
         * Constructs a {@code JSON} that will add application/json as the
         * content type header to the response.
         * @param res Original response
         */
        public Json(final Response res) {
            this(res, new Opt.Empty<>());
        }

        /**
         * Constructs a {@code JSON} that will add application/json as the
         * content type header to the response using the specified charset as
         * charset parameter value.
         * @param res Original response
         * @param charset The character set to add in the content type header
         */
        public Json(final Response res, final Charset charset) {
            this(res, new Opt.Single<>(charset));
        }

        /**
         * Constructs a {@code JSON} that will add application/json as the
         * content type header to the response using the specified charset as
         * charset parameter value if present.
         * @param res Original response
         * @param charset The character set to add in the content type header if
         *  present.
         */
        private Json(final Response res, final Opt<Charset> charset) {
            super(RsWithType.make(res, "application/json", charset));
        }

    }

    /**
     * Response decorator, with content type text/xml.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @since 0.30
     */
    public static final class Xml extends RsWrap {

        /**
         * Constructs a {@code XML} that will add text/xml as the content type
         * header to the response.
         * @param res Original response
         */
        public Xml(final Response res) {
            this(res, new Opt.Empty<>());
        }

        /**
         * Constructs a {@code XML} that will add text/xml as the content type
         * header to the response using the specified charset as charset
         * parameter value.
         * @param res Original response
         * @param charset The character set to add in the content type header
         */
        public Xml(final Response res, final Charset charset) {
            this(res, new Opt.Single<>(charset));
        }

        /**
         * Constructs a {@code XML} that will add text/xml as the content type
         * header to the response using the specified charset as charset
         * parameter value if present.
         * @param res Original response
         * @param charset The character set to add in the content type header if
         *  present.
         */
        private Xml(final Response res, final Opt<Charset> charset) {
            super(RsWithType.make(res, "text/xml", charset));
        }

    }

    /**
     * Response decorator, with content type text/plain.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @since 0.30
     */
    public static final class Text extends RsWrap {

        /**
         * Constructs a {@code Text} that will add text/plain as the content
         * type header to the response.
         * @param res Original response
         */
        public Text(final Response res) {
            this(res, new Opt.Empty<>());
        }

        /**
         * Constructs a {@code Text} that will add text/plain as the content
         * type header to the response using the specified charset as charset
         * parameter value.
         * @param res Original response
         * @param charset The character set to add in the content type header
         */
        public Text(final Response res, final Charset charset) {
            this(res, new Opt.Single<>(charset));
        }

        /**
         * Constructs a {@code Text} that will add text/plain as the content
         * type header to the response using the specified charset as charset
         * parameter value if present.
         * @param res Original response
         * @param charset The character set to add in the content type header if
         *  present.
         */
        private Text(final Response res, final Opt<Charset> charset) {
            super(RsWithType.make(res, "text/plain", charset));
        }

    }
}
