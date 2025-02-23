/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Matchers.
 *
 * <p>This package contains Hamcrest matchers for all key interfaces
 * in the framework. Use them as in this example:
 *
 * <pre> public class FooTest {
 *   &#64;Test
 *   public void returnsOK() {
 *     final Response response = new TkIndex().act(new RqFake());
 *     MatcherAssert.assertThat(
 *       response,
 *       new HmStatus(Matchers.equalTo(HttpURLConnection.HTTP_OK))
 *     );
 *   }
 * }</pre>
 *
 * @since 0.13
 */
package org.takes.facets.hamcrest;
