/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Take implementations and decorators.
 *
 * <p>This package contains various implementations of the Take interface,
 * which is the core abstraction in the Takes framework. A Take is an object
 * that accepts an HTTP request and returns an HTTP response. The package
 * provides numerous Take implementations and decorators that handle different
 * aspects of web application functionality including routing, static content
 * serving, authentication, compression, caching, and error handling.
 *
 * <p>Key components include:
 * <ul>
 * <li>Core implementations: TkEmpty, TkFixed, TkWrap, TkClasspath</li>
 * <li>Content serving: TkFiles, TkHtml, TkText, TkRedirect</li>
 * <li>Request processing: TkGzip, TkMeasured, TkVersioned, TkCached</li>
 * <li>Routing: TkFork for request dispatching based on various criteria</li>
 * <li>Security: TkAuth, TkSecure for authentication and authorization</li>
 * <li>Testing utilities: TkFailure for simulating errors</li>
 * </ul>
 *
 * @since 0.1
 */
package org.takes.tk;
