/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rs.RsWithBody;

/**
 * Take that serves static resources from Java classpath.
 *
 * <p>This {@link Take} implementation serves static resources bundled within
 * JAR files or available on the Java classpath. It provides a convenient way
 * to serve packaged resources such as web assets, configuration files, or
 * templates that are included as part of the application deployment.
 *
 * <p>The take resolves resource paths by combining a configured prefix
 * with the path component of the request URL. Resources are loaded using
 * the standard Java resource loading mechanism through {@code getResourceAsStream()}.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Serve resources from /static classpath location
 * new TkClasspath("/static");
 *
 * // Request: GET /css/style.css
 * // Resolves to: /static/css/style.css on classpath
 *
 * // Serve from package structure
 * new TkClasspath(MyClass.class);
 * // Uses package path: /com/example/mypackage/
 * }</pre>
 *
 * <p>Common use cases include:
 * <ul>
 *   <li>Serving bundled web assets (CSS, JavaScript, images)</li>
 *   <li>Template and configuration file serving</li>
 *   <li>Embedded documentation and help files</li>
 *   <li>Default resource fallbacks in web applications</li>
 *   <li>Plugin resource serving from JAR files</li>
 *   <li>Development and testing resource access</li>
 *   <li>Microservice resource bundling</li>
 * </ul>
 *
 * <p>Advantages over filesystem serving:
 * <ul>
 *   <li>Resources are packaged within application JAR files</li>
 *   <li>No external file dependencies or directory structures</li>
 *   <li>Resources are available regardless of working directory</li>
 *   <li>Supports serving from multiple JAR files in classpath</li>
 *   <li>Ideal for containerized and cloud deployments</li>
 * </ul>
 *
 * <p>The response includes appropriate HTTP headers with Content-Length
 * calculated from resource size. Binary resources are served efficiently
 * using stream-based responses to minimize memory usage.
 *
 * <p>If a requested resource is not found on the classpath, an
 * {@link org.takes.HttpException} with HTTP 404 status is thrown,
 * providing clear error information including the resource path that
 * was attempted.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkClasspath extends TkWrap {

    /**
     * Ctor.
     * Creates a classpath take with empty prefix (root of classpath).
     */
    public TkClasspath() {
        this("");
    }

    /**
     * Ctor.
     * @param base Base class whose package will be used as resource prefix
     */
    public TkClasspath(final Class<?> base) {
        this(
            String.format(
                "/%s", base.getPackage().getName().replace(".", "/")
            )
        );
    }

    /**
     * Ctor.
     * @param prefix Classpath prefix for resource resolution
     */
    public TkClasspath(final String prefix) {
        super(
            new Take() {
                @Override
                public Response act(final Request request) throws IOException {
                    final String name = String.format(
                        "%s%s", prefix, new RqHref.Base(request).href().path()
                    );
                    final InputStream input = this.getClass()
                        .getResourceAsStream(name);
                    if (input == null) {
                        throw new HttpException(
                            HttpURLConnection.HTTP_NOT_FOUND,
                            String.format("%s not found in classpath", name)
                        );
                    }
                    return new RsWithBody(input);
                }
            }
        );
    }

}
