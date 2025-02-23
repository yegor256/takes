/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Previous.
 *
 * <p>These classes may be useful when you want to redirect your user
 * to some location and remember what he/she wanted to see before. This
 * is especially useful during login. When the user is trying to open
 * some page, where access credentials are required, you throw
 * {@link org.takes.facets.forward.RsForward} to the home page,
 * with this class inside, with the original URL:
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
