/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import org.takes.Request;

/**
 * HTTP multipart form data parsing interface.
 *
 * <p>This interface provides methods to parse multipart/form-data requests,
 * typically used for file uploads and form submissions with binary data.
 * It allows access to individual parts by name and retrieval of all part names.
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
