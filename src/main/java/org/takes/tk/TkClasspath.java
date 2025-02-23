/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rs.RsWithBody;

/**
 * Take reading resources from classpath.
 *
 * <p>This "take" is trying to find the requested resource in
 * classpath and return it as an HTTP response with binary body, for example:
 *
 * <pre> new TkClasspath("/my");</pre>
 *
 * <p>This object will take query part of the arrived HTTP
 * {@link org.takes.Request} and concatenate it with the {@code "/my"} prefix.
 * For example, a request comes it and its query equals to
 * {@code "/css/style.css?eot"}. {@link TkClasspath}
 * will try to find a resource {@code "/my/css/style.css"} in classpath.
 *
 * <p>If such a resource is not found, {@link org.takes.HttpException}
 * will be thrown.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkClasspath extends TkWrap {

    /**
     * Ctor.
     */
    public TkClasspath() {
        this("");
    }

    /**
     * Ctor.
     * @param base Base class
     */
    public TkClasspath(final Class<?> base) {
        this(
            String.format(
                "/%s", base.getPackage().getName().replace(".", "/")
            )
        );
    }

    /**
     * Ctor.
     * @param prefix Prefix
     */
    public TkClasspath(final String prefix) {
        super(
            new Take() {
                @Override
                public Response act(final Request request) throws IOException {
                    final String name = String.format(
                        "%s%s", prefix, new RqHref.Base(request).href().path()
                    );
                    final InputStream input = this.getClass()
                        .getResourceAsStream(name);
                    if (input == null) {
                        throw new HttpException(
                            HttpURLConnection.HTTP_NOT_FOUND,
                            String.format("%s not found in classpath", name)
                        );
                    }
                    return new RsWithBody(input);
                }
            }
        );
    }

}
