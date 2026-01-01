/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.cactoos.Scalar;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Opt;
import org.takes.rq.RqHref;
import org.takes.tk.TkFixed;
import org.takes.tk.TkText;

/**
 * Fork by regular expression pattern.
 *
 * <p>Use this class in combination with {@link TkFork},
 * for example:
 *
 * <pre> Take take = new TkFork(
 *   new FkRegex("/home", new TkHome()),
 *   new FkRegex("/account", new TkAccount())
 * );</pre>
 *
 * <p>Each instance of {@link org.takes.facets.fork.FkRegex} is being
 * asked only once by {@link TkFork} whether the
 * request is good enough to be processed. If the request is suitable
 * for this particular fork, it will return the relevant
 * {@link org.takes.Take}.
 *
 * <p>Also, keep in mind that the second argument of the constructor may
 * be of type {@link TkRegex} and accept an
 * instance of {@link org.takes.facets.fork.RqRegex}, which makes it very
 * convenient to reuse regular expression matcher, for example:
 *
 * <pre> Take take = new TkFork(
 *   new FkRegex(
 *     "/file(.*)",
 *     new Target&lt;RqRegex&gt;() {
 *       &#64;Override
 *       public Response act(final RqRegex req) {
 *         // Here we immediately getting access to the
 *         // matcher that was used during parsing of
 *         // the incoming request
 *         final String file = req.matcher().group(1);
 *       }
 *     }
 *   )
 * );</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @see TkFork
 * @see TkRegex
 * @since 0.4
 */
@EqualsAndHashCode
public final class FkRegex implements Fork {

    /**
     * Pattern.
     */
    private final Pattern pattern;

    /**
     * Target.
     */
    private final Scalar<TkRegex> target;

    /**
     * Remove trailing slashes is optional.
     */
    private boolean removeslash;

    /**
     * Ctor.
     * @param ptn Pattern
     * @param text Text
     */
    public FkRegex(final String ptn, final String text) {
        this(
            Pattern.compile(ptn, Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            new TkText(text)
        );
    }

    /**
     * Ctor.
     * @param ptn Pattern
     * @param rsp Response
     * @since 0.16
     */
    public FkRegex(final String ptn, final Response rsp) {
        this(ptn, new TkFixed(rsp));
    }

    /**
     * Ctor.
     * @param ptn Pattern
     * @param rsp Response
     * @since 0.16
     */
    public FkRegex(final Pattern ptn, final Response rsp) {
        this(ptn, new TkFixed(rsp));
    }

    /**
     * Ctor.
     * @param ptn Pattern
     * @param that Take
     */
    public FkRegex(final String ptn, final Take that) {
        this(
            Pattern.compile(ptn, Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            that
        );
    }

    /**
     * Ctor.
     * @param ptn Pattern
     * @param that Take
     */
    public FkRegex(final Pattern ptn, final Take that) {
        this(
            ptn,
            (TkRegex) req -> that.act(req)
        );
    }

    /**
     * Ctor.
     * @param ptn Pattern
     * @param that Take
     */
    public FkRegex(final String ptn, final TkRegex that) {
        this(
            Pattern.compile(ptn, Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            that
        );
    }

    /**
     * Ctor.
     * @param ptn Pattern
     * @param that Take
     */
    public FkRegex(final Pattern ptn, final TkRegex that) {
        this(
            ptn,
            () -> that
        );
    }

    /**
     * Ctor.
     * @param ptn Pattern
     * @param that Take
     * @since 1.4
     */
    public FkRegex(final Pattern ptn, final Scalar<TkRegex> that) {
        this.pattern = ptn;
        this.target = that;
        this.removeslash = true;
    }

    /**
     * Allows disabling the standard way for handling trailing slashes.
     * @param enabled Enables/Disables the removal of a trailing slash.
     * @return FkRegex
     */
    public FkRegex setRemoveTrailingSlash(final boolean enabled) {
        this.removeslash = enabled;
        return this;
    }

    @Override
    public Opt<Response> route(final Request req) throws Exception {
        String path = new RqHref.Base(req).href().path();
        if (
            this.removeslash
                && path.length() > 1
                && path.charAt(path.length() - 1) == '/'
        ) {
            path = path.substring(0, path.length() - 1);
        }
        final Matcher matcher = this.pattern.matcher(path);
        final Opt<Response> resp;
        if (matcher.matches()) {
            resp = new Opt.Single<>(
                this.target.value().act(
                    new RqMatcher(matcher, req)
                )
            );
        } else {
            resp = new Opt.Empty<>();
        }
        return resp;
    }

    /**
     * Request with a matcher inside.
     *
     * @since 0.32.5
     */
    private static final class RqMatcher implements RqRegex {

        /**
         * Matcher.
         */
        private final Matcher mtr;

        /**
         * Original request.
         */
        private final Request req;

        /**
         * Ctor.
         * @param matcher Matcher
         * @param request Request
         */
        RqMatcher(final Matcher matcher, final Request request) {
            this.mtr = matcher;
            this.req = request;
        }

        @Override
        public Iterable<String> head() throws IOException {
            return this.req.head();
        }

        @Override
        public InputStream body() throws IOException {
            return this.req.body();
        }

        @Override
        public Matcher matcher() {
            return this.mtr;
        }
    }

}
