/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import org.takes.Request;

/**
 * HTTP multipart FORM data decoding.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @since 0.9
 */
public interface RqMultipart extends Request {

    /**
     * Get single part.
     * @param name Name of the part to get
     * @return List of parts (can be empty)
     */
    Iterable<Request> part(CharSequence name);

    /**
     * Get all part names.
     * @return All names
     */
    Iterable<String> names();

}
