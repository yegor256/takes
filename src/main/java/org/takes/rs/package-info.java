/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * HTTP response creation and decoration classes.
 *
 * <p>This package contains interfaces and implementations for building
 * and decorating HTTP responses in the Takes framework. It provides a
 * comprehensive set of response decorators following the decorator pattern
 * to add various functionalities such as content type handling, status
 * codes, headers, body content, formatting, and transformation.
 *
 * <p>Key components include:
 * <ul>
 * <li>Core classes: RsWrap (base decorator), ResponseOf (basic implementation)</li>
 * <li>Content decorators: RsWithBody, RsHtml, RsText, RsJson</li>
 * <li>Header decorators: RsWithHeader, RsWithHeaders, RsWithoutHeader, RsWithType</li>
 * <li>Status decorators: RsWithStatus, RsRedirect, RsEmpty</li>
 * <li>Formatting decorators: RsPrettyJson, RsPrettyXml, RsGzip</li>
 * <li>Utility decorators: RsPrint, RsBuffered, RsFluent, RsXslt</li>
 * <li>Template engines: RsVelocity</li>
 * <li>Testing utilities: RsBodyPrint, RsHeadPrint</li>
 * </ul>
 *
 * @since 0.1
 */
package org.takes.rs;
