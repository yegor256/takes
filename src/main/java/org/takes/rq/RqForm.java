/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import org.takes.Request;

/**
 * Interface for parsing HTML form data from HTTP requests.
 *
 * <p>This interface provides methods to decode form data from requests
 * with {@code application/x-www-form-urlencoded} content type as specified
 * in RFC 1738. It allows access to form parameters by name and retrieval
 * of all parameter names.
 *
 * <p>For {@code multipart/form-data} format use
 * {@link org.takes.rq.multipart.RqMtBase}.
 *
 * <p>It is highly recommended to use {@link org.takes.rq.RqGreedy}
 * decorator before passing request to this class.
 *
 * <p>All implementations must be immutable and thread-safe.
 *
 * @see <a href="http://www.w3.org/TR/html401/interact/forms.html">
 *     Forms in HTML</a>
 * @see org.takes.rq.RqGreedy
 * @since 0.9
 */
public interface RqForm extends Request {

    /**
     * Get a single parameter.
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
