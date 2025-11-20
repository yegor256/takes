/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.list.ListOf;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Opt;

/**
 * Fork take.
 *
 * <p>This is the implementation of {@link org.takes.Take} that
 * routes the requests to another take, using a collection of forks
 * to pick the right one. The best example is a routing by regular
 * expression, for example:
 *
 * <pre> Take take = new TkFork(
 *   new FkRegex("/home", new TkHome()),
 *   new FkRegex("/account", new TkAccount())
 * );</pre>
 *
 * <p>Here, {@link TkFork} will try to call these
 * "forks" one by one, asking whether they accept the request. The first
 * one that reacts will get control. Each "fork" is an implementation
 * of {@link org.takes.facets.fork.Fork}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @see org.takes.facets.fork.FkMethods
 * @see org.takes.facets.fork.FkRegex
 * @see org.takes.facets.fork.FkParams
 * @since 0.4
 */
@ToString(of = "forks")
@EqualsAndHashCode
public final class TkFork implements Take {

    /**
     * Patterns and their respective take.
     */
    private final Collection<Fork> forks;

    /**
     * Ctor.
     */
    public TkFork() {
        this(Collections.emptyList());
    }

    /**
     * Ctor.
     * @param frks Forks
     */
    public TkFork(final Fork... frks) {
        this(Arrays.asList(frks));
    }

    /**
     * Ctor.
     * @param frks Forks
     */
    public TkFork(final Collection<Fork> frks) {
        this.forks = new ListOf<>(frks);
    }

    @Override
    public Response act(final Request request) throws Exception {
        final Opt<Response> response = new FkChain(this.forks).route(request);
        if (response.has()) {
            return response.get();
        }
        throw new HttpException(HttpURLConnection.HTTP_NOT_FOUND);
    }
}
