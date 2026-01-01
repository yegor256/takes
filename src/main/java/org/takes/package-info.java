/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Takes, Java web development framework.
 *
 * <p>Takes is a pure object-oriented and immutable web development framework
 * for Java 6+ projects. The design of Takes is based on four fundamental
 * principles: 1) no NULLs, 2) no mutable classes, 3) no public static
 * methods, and 4) no type casting. Due to these principles, the design
 * is loosely coupled, highly cohesive, and easy to test. To start
 * working with the framework, check out our introduction page on GitHub:
 * <a href="https://github.com/yegor256/takes">README</a>.
 *
 * @since 0.1
 * @see <a href="http://www.takes.org">project site www.takes.org</a>
 * @see <a href="https://github.com/yegor256/takes">GitHub project</a>
 * @see <a href="http://www.yegor256.com/2015/03/22/takes-java-web-framework.html">Java Web App Architecture In Takes Framework</a>
 * @see <a href="https://en.wikipedia.org/wiki/Takes_(framework)">wikipedia about Takes framework</a>
 * @todo #998:30min Replace the usage of {@link java.lang.String}.format
 *  in the project and leverage {@link org.cactoos.text.FormattedText} decorator
 *  as it is an elegant object-oriented way.
 */
package org.takes;
