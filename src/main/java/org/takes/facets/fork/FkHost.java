/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import lombok.EqualsAndHashCode;
import org.cactoos.scalar.EqualsNullable;
import org.cactoos.text.Lowered;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Opt;
import org.takes.rq.RqHeaders;

/**
 * Fork by host name.
 *
 * <p>Use this class in combination with {@link TkFork},
 * for example:
 *
 * <pre> Take take = new TkFork(
 *   new FkHost("www.example.com", new TkText("home")),
 *   new FkHost("doc.example.com", new TkText("doc is here"))
 * );</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @see TkFork
 * @since 0.32
 */
@EqualsAndHashCode(callSuper = true)
public final class FkHost extends FkWrap {

    /**
     * Ctor.
     * @param host Host
     * @param take Take to use
     */
    public FkHost(final String host, final Take take) {
        super(FkHost.fork(host, take));
    }

    /**
     * Make fork.
     * @param host Host
     * @param take Take to use
     * @return Fork
     */
    private static Fork fork(final String host, final Take take) {
        return req -> {
            final String hst = new RqHeaders.Smart(req).single("host");
            final Opt<Response> ret;
            if (new EqualsNullable(new Lowered(host), new Lowered(hst)).value()) {
                ret = new Opt.Single<>(take.act(req));
            } else {
                ret = new Opt.Empty<>();
            }
            return ret;
        };
    }

}
