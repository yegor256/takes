/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.servlet;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import org.cactoos.io.InputOf;
import org.cactoos.io.OutputTo;
import org.cactoos.io.TeeInput;
import org.cactoos.iterable.Filtered;
import org.cactoos.list.ListOf;
import org.cactoos.scalar.LengthOf;
import org.cactoos.scalar.Unchecked;
import org.cactoos.text.Lowered;
import org.cactoos.text.StartsWith;
import org.cactoos.text.TextOf;
import org.takes.Response;
import org.takes.facets.cookies.RsWithCookie;
import org.takes.rs.RsWithHeader;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWithoutHeader;

/**
 * Fake HttpServletResponse implementation for testing.
 *
 * <p>This class provides a test double for {@link HttpServletResponse} that
 * wraps a Takes {@link Response} object. It's designed for unit testing
 * scenarios where you need to simulate servlet container response handling
 * without running an actual servlet container.
 *
 * <p>The implementation uses an {@link AtomicReference} to hold the response,
 * allowing it to be modified through servlet API calls (adding headers,
 * setting status, adding cookies, etc.) while maintaining thread safety.
 * These modifications create new decorated Takes response objects rather
 * than mutating state directly.
 *
 * <p>Key features:
 * <ul>
 *   <li>Wraps Takes {@link Response} to provide servlet {@link HttpServletResponse} API</li>
 *   <li>Supports adding headers, cookies, and setting HTTP status codes</li>
 *   <li>Provides access to response body through {@link ServletOutputStream}</li>
 *   <li>Thread-safe response modification using atomic references</li>
 *   <li>Minimal implementation focused on testing needs</li>
 * </ul>
 *
 * <p>Many methods throw {@link UnsupportedOperationException} as they
 * represent servlet container features not commonly needed in unit tests
 * (character encoding, buffering, locale handling, etc.).
 *
 * @since 1.14
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class HttpServletResponseFake implements HttpServletResponse {
    /**
     * A Takes response.
     */
    private final AtomicReference<Response> response;

    /**
     * Ctor.
     * @param resp A Takes Response object
     */
    public HttpServletResponseFake(final Response resp) {
        this.response = new AtomicReference<>(resp);
    }

    @Override
    public void addCookie(final Cookie cookie) {
        this.response.set(
            new RsWithCookie(
                this.response.get(),
                cookie.getName(),
                cookie.getValue()
            )
        );
    }

    @Override
    public void setHeader(final String name, final String value) {
        this.response.set(
            new RsWithHeader(
                new RsWithoutHeader(
                    this.response.get(),
                    name
                ),
                name,
                value
            )
        );
    }

    @Override
    public void setStatus(final int code) {
        this.response.set(
            new RsWithStatus(
                this.response.get(),
                code
            )
        );
    }

    @Override
    public void sendError(
        final int code,
        final String reason
    ) {
        this.response.set(
            new RsWithStatus(
                this.response.get(),
                code,
                reason
            )
        );
    }

    @Override
    public Collection<String> getHeaders(final String header) {
        final Iterable<String> head;
        try {
            head = this.response.get().head();
        } catch (final IOException ex) {
            throw new IllegalStateException(
                "Failed to read the head from the request",
                ex
            );
        }
        return new ListOf<>(
            new Filtered<>(
                hdr -> new StartsWith(
                    new Lowered(hdr),
                    new TextOf(String.format("%s", new Lowered(header)))
                ).value(),
                head
            )
        );
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new Unchecked<>(
            new LengthOf(
                new TeeInput(
                    new InputOf(this.response.get().body()),
                    new OutputTo(baos)
                )
            )
        ).value();
        return new ServletOutputStreamTo(baos);
    }

    @Override
    public String getHeader(final String header) {
        throw new UnsupportedOperationException("#getHeaders()");
    }

    @Override
    public boolean containsHeader(final String header) {
        throw new UnsupportedOperationException("#containsHeader()");
    }

    @Override
    public String encodeURL(final String url) {
        throw new UnsupportedOperationException("#encodeURL()");
    }

    @Override
    public String encodeRedirectURL(final String url) {
        throw new UnsupportedOperationException("#encodeRedirectURL()");
    }

    /**
     * Encode a URL.
     * @param url URL to be encoded
     * @return The encoded URL
     * @deprecated It should not be used
     */
    @Deprecated
    public String encodeUrl(final String url) {
        throw new UnsupportedOperationException("#encodeUrl()");
    }

    /**
     * Encode a redirect URL.
     * @param url URL to be encoded
     * @return The encoded redirect URL
     * @deprecated It should not be used
     */
    @Deprecated
    public String encodeRedirectUrl(final String url) {
        throw new UnsupportedOperationException("#encodeRedirectUrl()");
    }

    @Override
    public void sendError(final int code) {
        throw new UnsupportedOperationException("#sendError()");
    }

    @Override
    public void sendRedirect(final String location) {
        throw new UnsupportedOperationException("#sendRedirect()");
    }

    @Override
    public void setDateHeader(final String name, final long time) {
        throw new UnsupportedOperationException("#setDateHeader()");
    }

    @Override
    public void addDateHeader(final String name, final long date) {
        throw new UnsupportedOperationException("#addDateHeader()");
    }

    @Override
    public void addHeader(final String name, final String value) {
        throw new UnsupportedOperationException("#addHeader()");
    }

    @Override
    public void setIntHeader(final String name, final int value) {
        throw new UnsupportedOperationException("#setIntHeader()");
    }

    @Override
    public void addIntHeader(final String name, final int value) {
        throw new UnsupportedOperationException("#addIntHeader()");
    }

    @Override
    public void sendRedirect(
        final String location,
        final int scode,
        final boolean clearbuff
    ) {
        throw new UnsupportedOperationException("#sendRedirect()");
    }

    /**
     * Set the response code status and reason.
     * @param code The status code
     * @param reason The reason originates this code
     * @deprecated It should not be used
     */
    @Deprecated
    public void setStatus(final int code, final String reason) {
        throw new UnsupportedOperationException("#setStatus()");
    }

    @Override
    public int getStatus() {
        throw new UnsupportedOperationException("#getStatus()");
    }

    @Override
    public Collection<String> getHeaderNames() {
        throw new UnsupportedOperationException("#getHeaderNames()");
    }

    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException("#getCharacterEncoding()");
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("#getContentType()");
    }

    @Override
    public PrintWriter getWriter() {
        throw new UnsupportedOperationException("#getWriter()");
    }

    @Override
    public void setCharacterEncoding(final String encoding) {
        throw new UnsupportedOperationException("#setCharacterEncoding()");
    }

    @Override
    public void setContentLength(final int length) {
        throw new UnsupportedOperationException("#setContentLength()");
    }

    @Override
    public void setContentLengthLong(final long length) {
        throw new UnsupportedOperationException("#setContentLengthLong()");
    }

    @Override
    public void setContentType(final String type) {
        throw new UnsupportedOperationException("#setContentType()");
    }

    @Override
    public void setBufferSize(final int size) {
        throw new UnsupportedOperationException("#setBufferSize()");
    }

    @Override
    public int getBufferSize() {
        throw new UnsupportedOperationException("#getBufferSize()");
    }

    @Override
    public void flushBuffer() {
        throw new UnsupportedOperationException("#flushBuffer()");
    }

    @Override
    public void resetBuffer() {
        throw new UnsupportedOperationException("#resetBuffer()");
    }

    @Override
    public boolean isCommitted() {
        throw new UnsupportedOperationException("#isCommitted()");
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("#reset()");
    }

    @Override
    public void setLocale(final Locale locale) {
        throw new UnsupportedOperationException("#setLocale()");
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("#getLocale()");
    }
}
