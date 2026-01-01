/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Multipart request handling implementations.
 *
 * <p>This package contains implementations of the {@link org.takes.rq.RqMultipart}
 * interface for handling multipart/form-data HTTP requests. Multipart requests
 * are commonly used for file uploads and form submissions containing both text
 * fields and binary data. The classes in this package provide functionality
 * to parse multipart request bodies, extract individual parts, and access
 * their headers and content.
 *
 * <p>Key components include:
 * <ul>
 * <li>RqMtBase: Base implementation for parsing multipart requests</li>
 * <li>RqMtFake: Test implementation for creating fake multipart requests</li>
 * <li>RqMtSmart: Decorator with additional convenience methods</li>
 * <li>RqPart interface: Represents individual parts within a multipart request</li>
 * </ul>
 *
 * @since 0.33
 */
package org.takes.rq.multipart;
