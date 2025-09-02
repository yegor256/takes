/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * User agent matching utilities for content negotiation.
 *
 * <p>This package provides components for matching HTTP User-Agent headers
 * to enable content negotiation and browser-specific behavior. It allows
 * applications to serve different content or apply different logic based
 * on the client's user agent string, supporting responsive design and
 * browser compatibility handling.
 *
 * <p>Key components include:
 * <ul>
 * <li>AmAgent interface for user agent matching strategies</li>
 * <li>FkAgent for routing based on user agent patterns</li>
 * <li>Various agent matchers for different browsers and devices</li>
 * </ul>
 *
 * @since 1.7.2
 */
package org.takes.facets.fork.am;
