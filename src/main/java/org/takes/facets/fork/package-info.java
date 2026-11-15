/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Request routing and dispatching framework.
 *
 * <p>This package provides a powerful routing system based on the Fork pattern,
 * which allows dispatching HTTP requests to different Takes based on various
 * criteria. The {@link org.takes.facets.fork.TkFork} class accepts multiple
 * {@link org.takes.facets.fork.Fork} implementations, each checking whether it
 * can handle the request; the first matching fork wins and its response is
 * returned. If no fork matches the request, {@link org.takes.facets.fork.TkFork}
 * throws an {@link org.takes.HttpException} with HTTP status 404. This enables
 * flexible request routing based on paths, methods, headers,
 * parameters, and custom conditions.</p>
 *
 * <p>Key components include:</p>
 * <ul>
 * <li>Core classes: {@link org.takes.facets.fork.TkFork} for request dispatching,
 * {@link org.takes.facets.fork.Fork} interface for routing rules,
 * {@link org.takes.facets.fork.FkChain} for routing through a chain of forks,
 * {@link org.takes.facets.fork.RsFork} for a response picked the same way</li>
 * <li>Path-based routing: {@link org.takes.facets.fork.FkRegex} for regex patterns
 * over the URI, together with {@link org.takes.facets.fork.RqRegex} and
 * {@link org.takes.facets.fork.TkRegex}; {@link org.takes.facets.fork.FkFixed}
 * dispatches every request to the same take, regardless of the URI</li>
 * <li>Method-based routing: {@link org.takes.facets.fork.FkMethods} and
 * {@link org.takes.facets.fork.TkMethods} for HTTP method filtering</li>
 * <li>Header-based routing: {@link org.takes.facets.fork.FkHost} by host name,
 * {@link org.takes.facets.fork.FkAgent} by the "User-Agent" header,
 * {@link org.takes.facets.fork.FkHitRefresh} by the "X-Takes-HitRefresh" header,
 * {@link org.takes.facets.fork.FkAuthenticated} for logged-in users and
 * {@link org.takes.facets.fork.FkAnonymous} for anonymous ones</li>
 * <li>Content routing: {@link org.takes.facets.fork.FkTypes} by the "Accept" header
 * (with {@link org.takes.facets.fork.MediaType} and
 * {@link org.takes.facets.fork.MediaTypes}), {@link org.takes.facets.fork.FkEncoding}
 * by the "Accept-Encoding" header, {@link org.takes.facets.fork.FkContentType} by the
 * "Content-Type" header, with {@link org.takes.facets.fork.TkProduces} and
 * {@link org.takes.facets.fork.TkConsumes} as convenience decorators on top of them</li>
 * <li>Parameter routing: {@link org.takes.facets.fork.FkParams} for query parameters
 * and their values, matched by regular expressions</li>
 * <li>Utility forks: {@link org.takes.facets.fork.FkWrap} for decorating other forks</li>
 * </ul>
 *
 * @since 0.4
 */
package org.takes.facets.fork;
