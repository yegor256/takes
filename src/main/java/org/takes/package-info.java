/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Take, Java web development framework.
 *
 * <p>Take is a pure object-oriented and immutable web development framework
 * for Java 6+ projects. The design of Take is based on four fundamental
 * principles: 1) no NULLs, 2) no mutable classes, 3) no public static
 * method, and 4) no type casting. Due to these principles, the design
 * is loosely coupled, highly cohesive and easy to test. To start
 * working with the framework, check out our introduction page in Github:
 * <a href="https://github.com/yegor256/take">README</a>.
 *
 * @since 0.1
 * @see <a href="http://www.takes.org">project site www.takes.org</a>
 * @see <a href="https://github.com/yegor256/take">Github project</a>
 * @see <a href="http://www.yegor256.com/2015/03/22/takes-java-web-framework.html">Java Web App Architecture In Takes Framework</a>
 * @see <a href="https://en.wikipedia.org/wiki/Takes_(framework)">wikipedia about Takes framework</a>
 * @todo #998:30min Replace the usage of {@link java.lang.String}.format
 *  in the project and leverage {@link org.cactoos.text.FormattedText} decorator
 *  as it is an elegant object oriented way.
 */
package org.takes;
