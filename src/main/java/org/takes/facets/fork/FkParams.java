/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.util.Iterator;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Opt;
import org.takes.rq.RqHref;

/**
 * Fork by query params and their values, matched by regular express.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.4
 * @see TkFork
 */
@EqualsAndHashCode
public final class FkParams implements Fork {

    /**
     * Param name.
     */
    private final String name;

    /**
     * Pattern for param value.
     */
    private final Pattern pattern;

    /**
     * Take.
     */
    private final Take take;

    /**
     * Ctor.
     * @param param Name of param
     * @param ptn Pattern
     * @param that Take
     */
    public FkParams(final String param, final String ptn, final Take that) {
        this(param, Pattern.compile(ptn), that);
    }

    /**
     * Ctor.
     * @param param Name of param
     * @param ptn Pattern
     * @param that Take
     */
    public FkParams(final String param, final Pattern ptn, final Take that) {
        this.name = param;
        this.pattern = ptn;
        this.take = that;
    }

    @Override
    public Opt<Response> route(final Request req) throws Exception {
        final Iterator<String> params = new RqHref.Base(req).href()
            .param(this.name).iterator();
        final Opt<Response> resp;
        if (params.hasNext()
            && this.pattern.matcher(params.next()).matches()) {
            resp = new Opt.Single<>(this.take.act(req));
        } else {
            resp = new Opt.Empty<>();
        }
        return resp;
    }

}
