/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.rq.multipart;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import lombok.EqualsAndHashCode;
import org.takes.rq.RqLive;
import org.takes.rq.RqWithHeader;
import org.takes.rq.RqWrap;
import org.takes.rq.TempInputStream;

/**
 * Request with a temporary file as body. The temporary file will be deleted
 * automatically when the body of the request will be closed.
 * @since 0.33
 * @see org.takes.rq.RqLive
 * @see org.takes.rq.TempInputStream
 */
@EqualsAndHashCode(callSuper = true)
final class RqTemp extends RqWrap {

    /**
     * Creates a {@code RqTemp} with the specified temporary file.
     * @param file The temporary that will be automatically deleted when the
     *  body of the request will be closed.
     * @throws IOException If fails
     */
    RqTemp(final File file) throws IOException {
        super(
            new RqWithHeader(
                new RqLive(
                    new TempInputStream(
                        Files.newInputStream(file.toPath()),
                        file
                    )
                ),
                "Content-Length",
                String.valueOf(file.length())
            )
        );
    }
}
