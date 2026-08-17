/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Servlet container integration components.
 *
 * <p>This package provides integration between the Takes framework and
 * Java servlet containers (Tomcat, Jetty, etc.). It enables Takes
 * applications to run inside servlet containers while maintaining
 * the framework's lightweight and immutable design principles.
 *
 * <p>The main components include:
 *
 * <h2>Production Components</h2>
 * <ul>
 * <li>{@link org.takes.servlet.SrvTake} - Main servlet adapter that
 * allows Takes applications to run in servlet containers</li>
 * <li>{@link org.takes.servlet.RqFrom} - Converts HttpServletRequest
 * to Takes Request objects</li>
 * <li>{@link org.takes.servlet.ResponseOf} - Converts Takes Response
 * objects to HttpServletResponse</li>
 * </ul>
 *
 * <h2>Testing Components</h2>
 * <ul>
 * <li>{@link org.takes.servlet.HttpServletRequestFake} - Test double
 * for HttpServletRequest</li>
 * <li>{@link org.takes.servlet.HttpServletResponseFake} - Test double
 * for HttpServletResponse</li>
 * <li>{@link org.takes.servlet.ServletConnectionFake} - Test double
 * for ServletConnection</li>
 * <li>{@link org.takes.servlet.ServletInputStreamOf} - Adapter for
 * InputStream to ServletInputStream</li>
 * <li>{@link org.takes.servlet.ServletOutputStreamTo} - Adapter for
 * OutputStream to ServletOutputStream</li>
 * </ul>
 *
 * <p>This integration allows developers to deploy Takes applications
 * in enterprise servlet containers while benefiting from Takes'
 * object-oriented, immutable approach to HTTP request handling.
 *
 * @since 2.0
 */
package org.takes.servlet;
