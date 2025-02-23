/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.multipart;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cactoos.scalar.Unchecked;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Lowered;
import org.cactoos.text.StartsWith;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.misc.VerboseIterable;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqLengthAware;
import org.takes.rq.RqMultipart;

/**
 * Request decorator, that decodes FORM data from
 * {@code multipart/form-data} format (RFC 2045).
 *
 * <p>For {@code application/x-www-form-urlencoded}
 * format use {@link org.takes.rq.RqForm}.
 *
 * <p>It is highly recommended to use {@link org.takes.rq.RqGreedy}
 * decorator before passing request to this class.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.33
 * @see <a href="http://www.w3.org/TR/html401/interact/forms.html">
 *  Forms in HTML</a>
 * @see org.takes.rq.RqGreedy
 */
@lombok.EqualsAndHashCode(of = "origin")
@SuppressWarnings("PMD.ExcessiveImports")
public final class RqMtBase implements RqMultipart {
    /**
     * The encoding used to create the request.
     */
    private static final Charset ENCODING = Charset.forName("UTF-8");

    /**
     * Pattern to get boundary from header.
     */
    private static final Pattern BOUNDARY = Pattern.compile(
        ".*[^a-z]boundary=([^;]+).*"
    );

    /**
     * Pattern to get name from header.
     */
    private static final Pattern NAME = Pattern.compile(
        ".*[^a-z]name=\"([^\"]+)\".*"
    );

    /**
     * Carriage return constant.
     */
    private static final String CRLF = "\r\n";

    /**
     * Map of params and values.
     */
    private final Map<String, List<Request>> map;

    /**
     * Internal buffer.
     */
    private final ByteBuffer buffer;

    /**
     * InputStream based on request body.
     */
    private final InputStream stream;

    /**
     * Original request.
     */
    private final Request origin;

    /**
     * Ctor.
     * @param req Original request
     * @throws IOException If fails
     * @todo #950:30m Remove code from this ctor, leaving only
     *  initialization. Currently this constructor access body
     *  of the request and triggers its evaluation. This breaks
     *  composition of multiple request, as it can be seen in
     *  {@link RqMtFake}. When this task is done, remove
     *  explicit lazy evaluation for RqMtFake.
     * @checkstyle ExecutableStatementCountCheck (2 lines)
     */
    public RqMtBase(final Request req) throws IOException {
        this.origin = req;
        this.stream = new RqLengthAware(req).body();
        this.buffer = ByteBuffer.allocate(
            Math.min(8192, this.stream.available())
        );
        this.map = this.requests(req);
    }

    @Override
    public Iterable<Request> part(final CharSequence name) {
        final List<Request> values = this.map.getOrDefault(
            new UncheckedText(
                new Lowered(name.toString())
            ).asString(),
            Collections.emptyList()
        );
        final Iterable<Request> iter;
        if (values.isEmpty()) {
            iter = new VerboseIterable<>(
                Collections.emptyList(),
                new FormattedText(
                    "there are no parts by name \"%s\" among %d others: %s",
                    name, this.map.size(), this.map.keySet()
                )
            );
        } else {
            iter = new VerboseIterable<>(
                values,
                new FormattedText(
                    "there are just %d parts by name \"%s\"",
                    values.size(), name
                )
            );
        }
        return iter;
    }

    @Override
    public Iterable<String> names() {
        return this.map.keySet();
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.origin.head();
    }

    @Override
    public InputStream body() throws IOException {
        return new CloseMultipart(this.origin.body());
    }

    /**
     * Build a request for each part of the origin request.
     * @param req Origin request
     * @return The requests map that use the part name as a map key
     * @throws IOException If fails
     */
    private Map<String, List<Request>> requests(
        final Request req) throws IOException {
        final String header = new RqHeaders.Smart(req).single("Content-Type");
        final Unchecked<Boolean> multipart = new Unchecked<>(
            new StartsWith(
                new Lowered(header),
                new TextOf("multipart/form-data")
            )
        );
        if (!multipart.value()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format(
                    "RqMtBase can only parse multipart/form-data, while Content-Type specifies a different type: \"%s\"",
                    header
                )
            );
        }
        final Matcher matcher = RqMtBase.BOUNDARY.matcher(header);
        if (!matcher.matches()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format(
                    "boundary is not specified in Content-Type header: \"%s\"",
                    header
                )
            );
        }
        final ReadableByteChannel body = Channels.newChannel(this.stream);
        if (body.read(this.buffer) < 0) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                "failed to read the request body"
            );
        }
        final byte[] boundary = String.format(
            "%s--%s", RqMtBase.CRLF, matcher.group(1)
        ).getBytes(RqMtBase.ENCODING);
        this.buffer.flip();
        this.buffer.position(boundary.length - 2);
        final Collection<Request> requests = new LinkedList<>();
        while (this.buffer.hasRemaining()) {
            final byte data = this.buffer.get();
            if (data == '-') {
                break;
            }
            this.buffer.position(this.buffer.position() + 1);
            requests.add(this.make(boundary, body));
        }
        return RqMtBase.asMap(requests);
    }

    /**
     * Make a request.
     *  Scans the origin request until the boundary reached. Caches
     *  the  content into a temporary file and returns it as a new request.
     * @param boundary Boundary
     * @param body Origin request body
     * @return Request
     * @throws IOException If fails
     */
    private Request make(final byte[] boundary,
        final ReadableByteChannel body) throws IOException {
        final File file = File.createTempFile(
            RqMultipart.class.getName(), ".tmp"
        );
        try (WritableByteChannel channel = Files.newByteChannel(
            file.toPath(),
            StandardOpenOption.READ,
            StandardOpenOption.WRITE
        )
        ) {
            channel.write(
                ByteBuffer.wrap(
                    this.head().iterator().next().getBytes(RqMtBase.ENCODING)
                )
            );
            channel.write(
                ByteBuffer.wrap(RqMtBase.CRLF.getBytes(RqMtBase.ENCODING))
            );
            new CopyBytesUntilBoundary(
                channel,
                boundary,
                body,
                this.buffer
            ).copy();
        }
        return new RqTemp(file);
    }

    /**
     * Convert a list of requests to a map.
     * @param reqs Requests
     * @return Map of them
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private static Map<String, List<Request>> asMap(
        final Collection<Request> reqs) throws IOException {
        final Map<String, List<Request>> map = new HashMap<>(reqs.size());
        for (final Request req : reqs) {
            final String header =
                new RqHeaders.Smart(req).single("Content-Disposition");
            final Matcher matcher = RqMtBase.NAME.matcher(header);
            if (!matcher.matches()) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    String.format(
                        "\"name\" not found in Content-Disposition header: %s",
                        header
                    )
                );
            }
            final String name = matcher.group(1);
            if (!map.containsKey(name)) {
                map.put(name, new LinkedList<>());
            }
            map.get(name).add(req);
        }
        return map;
    }

    /**
     * Decorator allowing to close all the parts of the request.
     * @since 0.33
     */
    private class CloseMultipart extends FilterInputStream {

        /**
         * Creates a {@code CloseParts} with the specified input.
         * @param input The underlying input stream.
         */
        CloseMultipart(final InputStream input) {
            super(input);
        }

        @Override
        public void close() throws IOException {
            try {
                super.close();
            } finally {
                for (final List<Request> requests
                    : RqMtBase.this.map.values()) {
                    for (final Request request : requests) {
                        request.body().close();
                    }
                }
            }
        }
    }
}
