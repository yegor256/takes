/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
