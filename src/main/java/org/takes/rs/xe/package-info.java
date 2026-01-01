/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Xembly-based XML response generation utilities.
 *
 * <p>This package provides classes for generating XML responses using the
 * Xembly library. Xembly is an imperative language for XML manipulation
 * that allows building XML documents through a series of directives.
 * The classes in this package implement the XeSource interface to generate
 * Xembly directives that are then converted to XML responses.
 *
 * <p>Key components include:
 * <ul>
 * <li>Core interfaces: XeSource for generating Xembly directives</li>
 * <li>Response builders: RsXembly for creating XML responses from sources</li>
 * <li>Content generators: XeAppend, XeChain, XeWhen for composing XML content</li>
 * <li>Data transformers: XeTransform, XeLink for converting data to XML</li>
 * <li>Utility classes: XeMemory for caching, XeDate for date formatting</li>
 * </ul>
 *
 * @since 0.1
 */
package org.takes.rs.xe;
