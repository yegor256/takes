/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;
import org.takes.rs.ResponseOf;
import org.takes.rs.RsWrap;

/**
 * Response based on forks.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.6
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsFork extends RsWrap {

    /**
     * Ctor.
     * @param req Request
     * @param list List of forks
     */
    public RsFork(final Request req, final Fork... list) {
        this(req, Arrays.asList(list));
    }

    /**
     * Ctor.
     * @param req Request
     * @param list List of forks
     */
    public RsFork(final Request req, final Iterable<Fork> list) {
        super(
            new ResponseOf(
                () -> RsFork.pick(req, list).head(),
                () -> RsFork.pick(req, list).body()
            )
        );
    }

    /**
     * Pick the right one.
     * @param req Request
     * @param forks List of forks
     * @return Response
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private static Response pick(final Request req,
        final Iterable<Fork> forks) throws IOException {
        for (final Fork fork : forks) {
            try {
                final Opt<Response> rsps = fork.route(req);
                if (rsps.has()) {
                    return rsps.get();
                }
                //@checkstyle IllegalCatch (1 line)
            } catch (final Exception ex) {
                throw new IOException(ex);
            }
        }
        throw new HttpException(HttpURLConnection.HTTP_NOT_FOUND);
    }

}
