/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Authentication and authorization framework components.
 *
 * <p>This package provides a comprehensive authentication and authorization
 * framework for Takes applications. It includes interfaces and implementations
 * for identity management, authentication passes, request/response decorators
 * for authenticated contexts, and integration with various authentication
 * mechanisms including social media OAuth providers.
 *
 * <p>Key components include:
 * <ul>
 * <li>Core interfaces: Identity for user representation, Pass for authentication</li>
 * <li>Authentication decorators: TkAuth, RqAuth, RsAuth for authenticated contexts</li>
 * <li>Pass implementations: PsAll, PsChain, PsCookie for various auth strategies</li>
 * <li>Identity implementations: PsFixed, PsLogout for identity management</li>
 * <li>Request utilities: RqIdentity for extracting authenticated user information</li>
 * <li>Response utilities: RsLogout for clearing authentication</li>
 * </ul>
 *
 * @since 0.1
 */
package org.takes.facets.auth;
