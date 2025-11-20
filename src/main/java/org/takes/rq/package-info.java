/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * HTTP request processing and decoration classes.
 *
 * <p>This package contains interfaces and implementations for handling
 * HTTP requests in the Takes framework. It provides a comprehensive set
 * of request decorators that follow the decorator pattern to add various
 * functionalities such as header manipulation, body processing, caching,
 * parsing, and validation.
 *
 * <p>Key components include:
 * <ul>
 * <li>Core interfaces: Request, RqHeaders, RqForm, RqHref, RqMultipart</li>
 * <li>Request decorators: RqWrap and its various implementations</li>
 * <li>Parsing utilities: RqLive, RqRequestLine, RqMethod</li>
 * <li>Testing utilities: RqFake, RqEmpty</li>
 * <li>Stream utilities: CapInputStream, ChunkedInputStream, TempInputStream</li>
 * </ul>
 *
 * @since 0.1
 */
package org.takes.rq;
