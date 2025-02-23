/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import org.takes.Request;

/**
 * Request decorator that decodes FORM data from
 * {@code application/x-www-form-urlencoded} format (RFC 1738).
 *
 * <p>For {@code multipart/form-data} format use
 * {@link org.takes.rq.multipart.RqMtBase}.
 *
 * <p>It is highly recommended to use {@link org.takes.rq.RqGreedy}
 * decorator before passing request to this class.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @see <a href="http://www.w3.org/TR/html401/interact/forms.html">
 *     Forms in HTML</a>
 * @see org.takes.rq.RqGreedy
 * @since 0.9
 */
public interface RqForm extends Request {

    /**
     * Get single parameter.
     * @param name Parameter name
     * @return List of values (can be empty)
     * @throws IOException if something fails reading parameters
     */
    Iterable<String> param(CharSequence name) throws IOException;

    /**
     * Get all parameter names.
     * @return All names
     * @throws IOException if something fails reading parameters
     */
    Iterable<String> names() throws IOException;

}
