/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.multipart;

import java.io.File;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.takes.rq.RqWrap;

/**
 * Request with a temporary file as body. The temporary file will be deleted
 * automatically when the body of the request will be closed.
 * @see org.takes.rq.RqLive
 * @see org.takes.rq.TempInputStream
 * @since 0.33
 */
@EqualsAndHashCode(callSuper = true)
final class RqTemp extends RqWrap {

    /**
     * Creates a {@code RqTemp} with the specified temporary file.
     * @param file The temporary that will be automatically deleted when the
     *  body of the request will be closed
     * @throws IOException If fails
     */
    RqTemp(final File file) throws IOException {
        super(new LazyRq(file));
    }
}
