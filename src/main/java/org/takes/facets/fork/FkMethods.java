/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Opt;
import org.takes.rq.RqMethod;
import org.takes.tk.TkFixed;

/**
 * Fork by method matching.
 *
 * <p>Use this class in combination with {@link TkFork},
 * for example:
 *
 * <pre> Take take = new TkFork(
 *   new FkMethods("GET", new TkLoad()),
 *   new FkMethods("PUT", new TkSave())
 * );</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.4
 * @see TkFork
 */
@EqualsAndHashCode
public final class FkMethods implements Fork {

    /**
     * Methods to match.
     */
    private final Collection<String> methods;

    /**
     * Target.
     */
    private final Take take;

    /**
     * Ctor.
     * @param mtd Method
     * @param rsp Response
     * @since 0.22
     */
    public FkMethods(final String mtd, final Response rsp) {
        this(mtd, new TkFixed(rsp));
    }

    /**
     * Ctor.
     * @param mtd Method
     * @param tke Take
     */
    public FkMethods(final String mtd, final Take tke) {
        this(Arrays.asList(mtd.split(",")), tke);
    }

    /**
     * Ctor.
     * @param mtds Methods
     * @param tke Take
     */
    public FkMethods(final Collection<String> mtds, final Take tke) {
        this.methods = Collections.unmodifiableCollection(mtds);
        this.take = tke;
    }

    @Override
    public Opt<Response> route(final Request req) throws Exception {
        final String mtd = new RqMethod.Base(req).method();
        final Opt<Response> resp;
        if (this.methods.contains(mtd)) {
            resp = new Opt.Single<>(this.take.act(req));
        } else {
            resp = new Opt.Empty<>();
        }
        return resp;
    }

}
