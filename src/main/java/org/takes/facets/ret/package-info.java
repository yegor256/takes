/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Return location functionality for navigation tracking.
 *
 * <p>This package provides utilities for implementing "return to previous page"
 * functionality using cookies. {@link org.takes.facets.ret.RsReturn} decorates a
 * response with a cookie (named {@code RsReturn} by default) that stores a
 * URL-encoded return location and expires after one hour. The location is
 * validated against RFC 3987 when the response is rendered, so an invalid
 * location fails with an {@link java.io.IOException} at that moment. For
 * example, remember where the user came from when they open a page:</p>
 *
 * <pre> public Response act(final Request req) throws IOException {
 *   return new RsReturn(
 *     new RsText("here is the page"),
 *     new RqHref.Base(req).href().toString()
 *   );
 * }</pre>
 *
 * <p>Later, wrap your application into {@link org.takes.facets.ret.TkReturn}:
 * every request carrying the cookie is answered with a redirect to the stored
 * location and the wrapper then responds with an empty cookie that attempts to
 * clear it. Mind that this clearing response does not repeat the
 * {@code Path=/} attribute set by {@link org.takes.facets.ret.RsReturn}, so the
 * browser only drops a copy scoped to the current request path and keeps
 * sending the original root cookie, which means the redirect may happen more
 * than once; requests without the cookie go to the wrapped take as usual.</p>
 *
 * @since 0.20
 */
package org.takes.facets.ret;
