/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.flash;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import lombok.EqualsAndHashCode;
import org.cactoos.text.FormattedText;
import org.takes.Response;
import org.takes.facets.cookies.RsWithCookie;
import org.takes.misc.Expires;
import org.takes.rs.RsWrap;

/**
 * A response decorator that adds flash messages to HTTP responses via cookies.
 *
 * <p>This class implements the flash message mechanism by adding Set-Cookie headers
 * containing temporary messages that persist between HTTP requests. The flash concept,
 * borrowed from Ruby on Rails, enables passing temporary variables between requests,
 * which is particularly useful for redirect scenarios.
 *
 * <p>Flash messages are ideal for displaying success or error notifications after
 * form submissions or other user actions. The mechanism uses cookies to maintain
 * state temporarily, making it unsuitable for stateless components like RESTful
 * services.
 *
 * <p>Basic usage example:
 *
 * <pre>public final class TkDiscussion implements Take {
 *   &#64;Override
 *   public Response act(final Request req) throws IOException {
 *     return new RsForward(new RsFlash("thanks for the post"));
 *   }
 * }</pre>
 *
 * <p>The decorator adds a Set-Cookie header with the message in URL-encoded format,
 * combined with a logging level for severity indication:
 *
 * <pre>public final class TkDiscussion implements Take {
 *   &#64;Override
 *   public Response act(final Request req) throws IOException {
 *     return new RsForward(
 *       new RsFlash(
 *         "can't save your post, sorry",
 *         java.util.logging.Level.SEVERE
 *       )
 *     );
 *   }
 * }</pre>
 *
 * <p>The resulting HTTP response will contain:
 *
 * <pre> HTTP/1.1 303 See Other
 * Set-Cookie: RsFlash=can%27t%20save%20your%20post%2C%20sorry/SEVERE</pre>
 *
 * <p>The default cookie name is {@code RsFlash}, but it can be customized using
 * appropriate constructors. To clean up cookies after consumption, decorate your
 * {@code Take} with {@link TkFlash}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class RsFlash extends RsWrap {

    /**
     * Text format.
     */
    private static final String TEXT_FORMAT = "%s/%s";

    /**
     * To string.
     */
    private final CharSequence text;

    /**
     * Constructs a {@code RsFlash} with the specified message.
     *
     * <p>By default it will use {@code RsFlash} as cookie name.</p>
     *
     * @param msg Message to show
     * @throws UnsupportedEncodingException In case the default encoding is not
     *  supported
     */
    public RsFlash(final CharSequence msg)
        throws UnsupportedEncodingException {
        this(msg, Level.INFO);
    }

    /**
     * Constructs a {@code RsFlash} with the specified message and expiration
     * date of the cookie.
     *
     * <p>By default it will use {@code RsFlash} as cookie name.</p>
     *
     * @param msg Message
     * @param expires Date of the cookie
     * @throws UnsupportedEncodingException In case the default encoding is not
     *  supported
     */
    public RsFlash(final CharSequence msg, final Expires.Date expires)
        throws UnsupportedEncodingException {
        this(msg, Level.INFO, expires);
    }

    /**
     * Constructs a {@code RsFlash} with the specified error.
     *
     * <p>The error is converted into a flash message by calling
     * {@link Throwable#getLocalizedMessage()}}.</p>
     *
     * <p>By default it will use {@code RsFlash} as cookie name and expiration
     * date of the cookie will be 1 hour after instance creation.</p>
     *
     * @param err Error
     * @throws UnsupportedEncodingException In case the default encoding is not
     *  supported
     */
    public RsFlash(final Throwable err)
        throws UnsupportedEncodingException {
        this(
            err,
            new Expires.Date(
                System.currentTimeMillis()
                    + TimeUnit.HOURS.toMillis(1L)
            )
        );
    }

    /**
     * Constructs a {@code RsFlash} with the specified error and cookie
     * expiration date.
     *
     * <p>The error is converted into a flash message by calling
     * {@link Throwable#getLocalizedMessage()}}.</p>
     *
     * <p>By default it will use {@code RsFlash} as cookie name.</p>
     *
     * @param err Error
     * @param expires Date of the cookie
     * @throws UnsupportedEncodingException In case the default encoding is not
     *  supported
     * @since 2.0
     */
    public RsFlash(final Throwable err, final Expires.Date expires)
        throws UnsupportedEncodingException {
        this(err, Level.SEVERE, expires);
    }

    /**
     * Constructs a {@code RsFlash} with the specified error and logging level.
     *
     * <p>The error is converted into a flash message by calling
     * {@link Throwable#getLocalizedMessage()}}.</p>
     *
     * <p>By default it will use {@code RsFlash} as cookie name and expiration
     * date of the cookie will be 1 hour after instance creation.</p>
     *
     * @param err Error
     * @param level Level
     * @throws UnsupportedEncodingException In case the default encoding is not
     *  supported
     * @since 0.17
     */
    public RsFlash(final Throwable err, final Level level)
        throws UnsupportedEncodingException {
        this(
            err.getLocalizedMessage(),
            level,
            new Expires.Date(
                System.currentTimeMillis()
                    + TimeUnit.HOURS.toMillis(1L)
            )
        );
    }

    /**
     * Constructs a {@code RsFlash} with the specified error, logging level
     * and cookie expiration date.
     *
     * <p>The error is converted into a flash message by calling
     * {@link Throwable#getLocalizedMessage()}}.</p>
     * <p>By default it will use {@code RsFlash} as cookie name.</p>
     *
     * @param err Error
     * @param level Level
     * @param expires Date of the cookie
     * @throws UnsupportedEncodingException In case the default encoding is
     *  not supported
     * @since 2.0
     */
    public RsFlash(final Throwable err, final Level level,
        final Expires.Date expires) throws UnsupportedEncodingException {
        this(err.getLocalizedMessage(), level, expires);
    }

    /**
     * Constructs a {@code RsFlash} with the specified message and logging
     * level.
     *
     * <p>By default it will use {@code RsFlash} as cookie name and default
     * cookie expiration date will be 1 hour after instance creation.</p>
     *
     * @param msg Message
     * @param level Level
     * @throws UnsupportedEncodingException In case the default encoding is not
     *  supported
     */
    public RsFlash(final CharSequence msg, final Level level)
        throws UnsupportedEncodingException {
        this(msg, level, RsFlash.class.getSimpleName());
    }

    /**
     * Constructs a {@code RsFlash} with the specified message, logging level
     * and cookie expiration date.
     *
     * <p>By default it will use {@code RsFlash} as cookie name
     *
     * @param msg Message
     * @param level Level
     * @param expires Date of the cookie
     * @throws UnsupportedEncodingException In case the default encoding is
     *  not supported
     * @since 2.0
     */
    public RsFlash(final CharSequence msg, final Level level,
        final Expires.Date expires) throws UnsupportedEncodingException {
        this(
            msg,
            level,
            RsFlash.class.getSimpleName(),
            expires
        );
    }

    /**
     * Constructs a {@code RsFlash} with the specified message, logging level
     * and cookie name.
     *
     * <p>By default cookie expiration will be 1 hour after object creation.
     *
     * @param msg Message
     * @param level Level
     * @param cookie Cookie name
     * @throws UnsupportedEncodingException In case the default encoding is
     *  not supported
     * @since 2.0
     */
    public RsFlash(final CharSequence msg, final Level level,
        final String cookie) throws UnsupportedEncodingException {
        this(
            msg,
            level,
            cookie,
            new Expires.Date(
                System.currentTimeMillis()
                    + TimeUnit.HOURS.toMillis(1L)
            )
        );
    }

    /**
     * Constructs a {@code RsFlash} with the specified message, logging level
     * and cookie name.
     * @param msg Message
     * @param level Level
     * @param cookie Cookie name
     * @param expires Date of the cookie
     * @throws UnsupportedEncodingException In case the default encoding is not
     *  supported
     * @since 2.0
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public RsFlash(final CharSequence msg, final Level level,
        final String cookie, final Expires.Date expires)
        throws UnsupportedEncodingException {
        super(RsFlash.make(msg, level, cookie, expires));
        this.text = String.format(RsFlash.TEXT_FORMAT, level, msg);
    }

    @Override
    public String toString() {
        return String.format(
            "%s(super=%s, text=%s)",
            RsFlash.class.getSimpleName(), super.toString(), this.text
        );
    }

    /**
     * Creates a response with flash message cookie.
     * @param msg The flash message text
     * @param level The logging level for message severity
     * @param cookie The cookie name to use
     * @param expires The cookie expiration date
     * @return A response with the Set-Cookie header containing the flash message
     * @throws UnsupportedEncodingException If URL encoding fails
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    private static Response make(final CharSequence msg, final Level level,
        final String cookie, final Expires.Date expires)
        throws UnsupportedEncodingException {
        return new RsWithCookie(
            cookie,
            new FormattedText(
                RsFlash.TEXT_FORMAT,
                URLEncoder.encode(
                    msg.toString(),
                    Charset.defaultCharset().name()
                ),
                level.getName()
            ).toString(),
            "Path=/",
            expires.print()
        );
    }
}
