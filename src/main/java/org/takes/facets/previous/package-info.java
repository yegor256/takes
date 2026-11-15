/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Previous page tracking utilities for user navigation.
 *
 * <p>These classes provide functionality for redirecting users to their previous
 * location while remembering where they originally wanted to go. This is
 * particularly useful during authentication flows. When a user attempts to access
 * a page requiring credentials, you can send them to the login page while storing
 * their original destination in a {@code TkPrevious} cookie, using
 * {@link org.takes.facets.previous.RsPrevious} with the original URL:</p>
 *
 * <pre> if (not_logged_in) {
 *   return new RsPrevious(
 *     new RsRedirect("/login"),
 *     new RqHref(req).href().toString()
 *   );
 * }</pre>
 *
 * <p>The location is URL-encoded in the cookie, which expires after one hour.
 * Then, you decorate your application with
 * {@link org.takes.facets.previous.TkPrevious} and that's it: every request
 * carrying the cookie is answered with a redirect to the stored location, the
 * cookie is expired immediately after use, and requests without the cookie go
 * to the wrapped take as usual.</p>
 *
 * @since 1.10
 */
package org.takes.facets.previous;
