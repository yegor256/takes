/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.facets.forward;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import org.cactoos.text.FormattedText;
import org.takes.HttpException;
import org.takes.Response;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsWithHeader;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWithoutHeader;

/**
 * Forwarding response.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true, of = "origin")
@SuppressWarnings("serial")
public class RsForward extends HttpException implements Response {

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = 7676888610908953700L;

    /**
     * Home page, by default.
     */
    private static final String HOME = "/";

    /**
     * Original response.
     */
    private final transient Response origin;

    /**
     * Ctor.
     */
    public RsForward() {
        this(new RsEmpty());
    }

    /**
     * Ctor.
     * @param res Original response
     */
    public RsForward(final Response res) {
        this(res, RsForward.HOME);
    }

    /**
     * Ctor.
     * @param res Original response
     * @since 0.14
     */
    public RsForward(final RsForward res) {
        this(res.origin);
    }

    /**
     * Ctor.
     * @param res Original response
     * @param loc Location
     */
    public RsForward(final Response res, final CharSequence loc) {
        this(res, HttpURLConnection.HTTP_SEE_OTHER, loc);
    }

    /**
     * Ctor.
     * @param res Original response
     * @param loc Location
     * @since 0.14
     */
    public RsForward(final RsForward res, final CharSequence loc) {
        this(res.origin, loc);
    }

    /**
     * Ctor.
     * @param loc Location
     */
    public RsForward(final CharSequence loc) {
        this(HttpURLConnection.HTTP_SEE_OTHER, loc);
    }

    /**
     * Ctor.
     * @param code HTTP status code
     * @param loc Location
     */
    public RsForward(final int code, final CharSequence loc) {
        this(new RsEmpty(), code, loc);
    }

    /**
     * Ctor.
     * @param res Original
     * @param code HTTP status code
     * @since 0.17
     */
    public RsForward(final Response res, final int code) {
        this(res, code, RsForward.HOME);
    }

    /**
     * Ctor.
     * @param res Original
     * @param code HTTP status code
     * @param loc Location
     * @since 0.14
     */
    public RsForward(final RsForward res, final int code,
        final CharSequence loc) {
        this(res.origin, code, loc);
    }

    /**
     * Ctor.
     * @param res Original
     * @param code HTTP status code
     * @param loc Location
     */
    public RsForward(final Response res, final int code,
        final CharSequence loc) {
        super(code, String.format("[%3d] %s %s", code, loc, res.toString()));
        this.origin = new RsWithHeader(
            new RsWithoutHeader(
                new RsWithStatus(res, code),
                "Location"
            ),
            new FormattedText(
                "Location: %s", loc
            ).toString()
        );
    }

    @Override
    public final Iterable<String> head() throws IOException {
        return this.origin.head();
    }

    @Override
    public final InputStream body() throws IOException {
        return this.origin.body();
    }

    @Override
    public final String toString() {
        return super.toString();
    }

    /**
     * Set default object to write serializated data.
     * @param stream Stream to write
     * @throws IOException if fails
     */
    private static void writeObject(
        final ObjectOutputStream stream
    ) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Set default object to read serializated data.
     * @param stream Stream to read
     * @throws IOException if fails
     * @throws ClassNotFoundException if class doesn't exists
     */
    private static void readObject(
        final ObjectInputStream stream
    ) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }

}
