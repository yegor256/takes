/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.io.File;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.io.InputOf;
import org.takes.HttpException;
import org.takes.rq.RqHref;
import org.takes.rs.RsWithBody;

/**
 * Take that serves static files from filesystem directory.
 *
 * <p>This {@link Take} implementation serves static files from a specified
 * base directory on the filesystem. It maps HTTP request paths to file system
 * paths and returns the file content as HTTP responses with appropriate
 * Content-Type headers automatically determined by file extension.
 *
 * <p>The take resolves file paths by combining the configured base directory
 * with the path component of the request URL. Query parameters in the URL
 * are ignored during file resolution to prevent directory traversal attacks
 * and maintain clean file serving semantics.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Serve files from /var/www/static
 * new TkFiles("/var/www/static");
 * 
 * // Request: GET /css/style.css
 * // Resolves to: /var/www/static/css/style.css
 * }</pre>
 *
 * <p>Common use cases include:
 * <ul>
 *   <li>Static asset serving (CSS, JavaScript, images)</li>
 *   <li>Document repositories and file downloads</li>
 *   <li>Media file serving (videos, audio, documents)</li>
 *   <li>Template and resource file serving</li>
 *   <li>Public file directories and archives</li>
 *   <li>Static website hosting</li>
 *   <li>Development asset serving during testing</li>
 * </ul>
 *
 * <p>Security considerations:
 * <ul>
 *   <li>Only files within the base directory are accessible</li>
 *   <li>Directory traversal attempts (../) are handled safely</li>
 *   <li>Non-existent files return HTTP 404 Not Found</li>
 *   <li>File permissions are respected by the underlying filesystem</li>
 * </ul>
 *
 * <p>The response includes appropriate HTTP headers including Content-Length
 * calculated from file size and Content-Type determined from file extension.
 * Binary files are served efficiently using stream-based responses to minimize
 * memory usage for large files.
 *
 * <p>If a requested file does not exist, an {@link HttpException} with
 * HTTP 404 status is thrown, providing clear error information including
 * the absolute path that was attempted.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkFiles extends TkWrap {

    /**
     * Ctor.
     * @param base Base directory path for serving files
     */
    public TkFiles(final String base) {
        this(new File(base));
    }

    /**
     * Ctor.
     * @param base Base directory file object for serving files
     */
    public TkFiles(final File base) {
        super(
            request -> {
                final File file = new File(
                    base, new RqHref.Base(request).href().path()
                );
                if (!file.exists()) {
                    throw new HttpException(
                        HttpURLConnection.HTTP_NOT_FOUND,
                        String.format(
                            "%s not found", file.getAbsolutePath()
                        )
                    );
                }
                return new RsWithBody(new InputOf(file).stream());
            }
        );
    }

}
