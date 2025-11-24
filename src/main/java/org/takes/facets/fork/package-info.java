/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Request routing and dispatching framework.
 *
 * <p>This package provides a powerful routing system based on the Fork pattern,
 * which allows dispatching HTTP requests to different Takes based on various
 * criteria. The TkFork class accepts multiple Fork implementations, each
 * checking if it can handle the request and providing the appropriate Take.
 * This enables flexible request routing based on paths, methods, headers,
 * parameters, and custom conditions.
 *
 * <p>Key components include:
 * <ul>
 * <li>Core classes: TkFork for request dispatching, Fork interface for routing rules</li>
 * <li>Path-based routing: FkRegex for regex patterns, FkFixed for exact paths</li>
 * <li>Method-based routing: FkMethods for HTTP method filtering</li>
 * <li>Content routing: FkTypes for content-type based routing, FkEncoding for encodings</li>
 * <li>Parameter routing: FkParams for query parameters, FkAuthenticated for auth status</li>
 * <li>Utility forks: FkChain for sequential checking, FkWrap for decorating</li>
 * </ul>
 *
 * @since 0.4
 */
package org.takes.facets.fork;
