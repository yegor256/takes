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
package org.takes.tk;

import java.io.File;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.io.InputOf;
import org.takes.HttpException;
import org.takes.rq.RqHref;
import org.takes.rs.RsWithBody;

/**
 * Take reading resources from directory.
 *
 * <p>This "take" is trying to find the requested resource in
 * file system and return it as an HTTP response with binary body, for example:
 *
 * <pre> new TkFiles("/tmp");</pre>
 *
 * <p>This object will take query part of the arrived HTTP
 * {@link org.takes.Request} and concatenate it with the {@code "/tmp"} prefix.
 * For example, a request comes it and its query equals to
 * {@code "/css/style.css?eot"}. {@link TkFiles}
 * will try to find a resource {@code "/tmp/css/style.css"} on disc.
 *
 * <p>If such a resource is not found, {@link HttpException}
 * will be thrown.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkFiles extends TkWrap {

    /**
     * Ctor.
     * @param base Base directory
     */
    public TkFiles(final String base) {
        this(new File(base));
    }

    /**
     * Ctor.
     * @param base Base directory
     */
    public TkFiles(final File base) {
        super(
            request -> {
                final File file = new File(
                    base, new RqHref.Base(request).href().path()
                );
                if (!file.exists()) {
                    throw new HttpException(
                        HttpURLConnection.HTTP_NOT_FOUND,
                        String.format(
                            "%s not found", file.getAbsolutePath()
                        )
                    );
                }
                return new RsWithBody(new InputOf(file).stream());
            }
        );
    }

}
