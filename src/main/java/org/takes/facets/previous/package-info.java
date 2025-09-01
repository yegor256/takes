/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Previous page tracking utilities for user navigation.
 *
 * <p>These classes provide functionality for redirecting users to their previous
 * location while remembering where they originally wanted to go. This is
 * particularly useful during authentication flows. When users attempt to access
 * pages requiring credentials, you can redirect them to the login page while
 * storing their original destination using {@link org.takes.facets.forward.RsForward}
 * with the original URL:
 *
 * <pre> if (not_logged_id) {
 *   throw new RsForward(
 *     new RsWithPrevious(
 *       new RsFlash("You must be logged in!")
 *     )
 *   );
 * }</pre>
 *
 * <p>Then, you decorate your application with
 * {@link org.takes.facets.previous.TkPrevious} and that's it.
 *
 * @since 0.10
 */
package org.takes.facets.previous;
