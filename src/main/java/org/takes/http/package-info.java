/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * HTTP server components.
 *
 * <p>This package provides the core HTTP server implementation for the Takes
 * framework. It includes front-end components that handle incoming connections
 * and back-end components that process requests. The main components are:
 *
 * <ul>
 *   <li>{@link org.takes.http.Front} - Interface for server front-ends that
 *       accept incoming connections</li>
 *   <li>{@link org.takes.http.Back} - Interface for back-ends that process
 *       individual socket connections</li>
 *   <li>{@link org.takes.http.Exit} - Interface for controlling when the
 *       server should stop</li>
 * </ul>
 *
 * @since 0.1
 */
package org.takes.http;
