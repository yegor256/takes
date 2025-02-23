/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
