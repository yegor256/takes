/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.util.ResourceBundle;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Take;
import org.takes.rs.RsWithHeader;

/**
 * Take decorator that adds framework version information to responses.
 *
 * <p>This {@link Take} decorator wraps another take and automatically
 * adds an HTTP header containing the version information of the Takes
 * framework to all responses. This is useful for debugging, monitoring,
 * and identifying which version of the framework is serving requests.
 *
 * <p>The version information includes the framework version number,
 * revision identifier, and build date, automatically extracted from
 * the framework's resource bundle at runtime. The header format follows
 * the pattern: "version revision date".
 *
 * <p>Example usage:
 * <pre>{@code
 * // Add version header with default name "X-Takes-Version"
 * new TkVersioned(new TkText("Hello World"));
 * // Response will include: X-Takes-Version: 2.0.1 abc123 2025-01-01
 *
 * // Add version header with custom name
 * new TkVersioned(
 *     new TkHtml("<h1>API</h1>"),
 *     "X-Framework-Version"
 * );
 * // Response will include: X-Framework-Version: 2.0.1 abc123 2025-01-01
 * }</pre>
 *
 * <p>Common use cases include:
 * <ul>
 *   <li>Production debugging and troubleshooting</li>
 *   <li>API versioning and compatibility tracking</li>
 *   <li>Monitoring and observability in microservices</li>
 *   <li>Security auditing and vulnerability assessment</li>
 *   <li>Development and testing environment identification</li>
 *   <li>Client-side framework detection</li>
 *   <li>Support ticket investigation</li>
 * </ul>
 *
 * <p>The version information is loaded once during class initialization
 * and cached for performance. This ensures minimal runtime overhead
 * while providing consistent version reporting across all requests.
 *
 * <p>The decorator preserves all aspects of the original response including
 * status code, existing headers, and body content. The version header is
 * added without modifying or removing existing headers from the wrapped
 * take's response.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.4
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkVersioned extends TkWrap {

    /**
     * Version of the library.
     */
    private static final String VERSION = TkVersioned.make();

    /**
     * Ctor.
     * Creates versioned take with default header name "X-Takes-Version".
     * @param take Original take to wrap
     */
    public TkVersioned(final Take take) {
        this(take, "X-Takes-Version");
    }

    /**
     * Ctor.
     * @param take Original take to wrap
     * @param header Custom header name for version information
     */
    public TkVersioned(final Take take, final String header) {
        super(
            req -> new RsWithHeader(
                take.act(req), header, TkVersioned.VERSION
            )
        );
    }

    /**
     * Make a version.
     * Creates version string from resource bundle information.
     * @return Version string containing version, revision, and date
     */
    private static String make() {
        final ResourceBundle res =
            ResourceBundle.getBundle("org.takes.version");
        return String.format(
            "%s %s %s",
            res.getString("version"),
            res.getString("revision"),
            res.getString("date")
        );
    }

}
