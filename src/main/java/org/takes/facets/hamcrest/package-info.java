/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Hamcrest matchers for testing Takes components.
 *
 * <p>This package contains Hamcrest matchers for all key interfaces
 * in the framework, for both requests and responses:
 * {@link org.takes.facets.hamcrest.HmRsStatus} tests the status code of a
 * response, {@link org.takes.facets.hamcrest.HmHeader} tests headers of any
 * item implementing {@link org.takes.Head}, {@link org.takes.facets.hamcrest.HmBody}
 * tests the body of any item implementing {@link org.takes.Body},
 * {@link org.takes.facets.hamcrest.HmRqTextBody} and
 * {@link org.takes.facets.hamcrest.HmRsTextBody} test textual bodies of requests
 * and responses, while {@link org.takes.facets.hamcrest.HmRqCookie} and
 * {@link org.takes.facets.hamcrest.HmRsCookie} test a single cookie of a request
 * or a response. Use them as shown in this example:</p>
 *
 * <pre> public class FooTest {
 *   &#64;Test
 *   public void returnsOK() {
 *     final Response response = new TkIndex().act(new RqFake());
 *     MatcherAssert.assertThat(
 *       response,
 *       new HmRsStatus(HttpURLConnection.HTTP_OK)
 *     );
 *   }
 * }</pre>
 *
 * @since 0.13
 */
package org.takes.facets.hamcrest;
