/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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
package org.takes.facets.fork;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
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
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.4
 * @see TkFork
 * @see TkRegex
 */
@EqualsAndHashCode(of = { "pattern", "target" })
public final class FkRegex implements Fork {

    /**
     * Pattern.
     */
    private final transient Pattern pattern;

    /**
     * Target.
     */
    private final transient TkRegex target;

    /**
     * Ctor.
     * @param ptn Pattern
     * @param text Text
     */
    public FkRegex(final String ptn, final String text) {
        this(Pattern.compile(ptn), new TkText(text));
    }

    /**
     * Ctor.
     * @param ptn Pattern
     * @param tks Take
     */
    public FkRegex(final String ptn, final Take tks) {
        this(Pattern.compile(ptn), tks);
    }

    /**
     * Ctor.
     * @param ptn Pattern
     * @param tks Take
     */
    public FkRegex(final Pattern ptn, final Take tks) {
        this(
            ptn,
            new TkRegex() {
                @Override
                public Response act(final RqRegex req) throws IOException {
                    return tks.act(req);
                }
            }
        );
    }

    /**
     * Ctor.
     * @param ptn Pattern
     * @param tgt Take
     */
    public FkRegex(final String ptn, final TkRegex tgt) {
        this(Pattern.compile(ptn), tgt);
    }

    /**
     * Ctor.
     * @param ptn Pattern
     * @param tgt Take
     */
    public FkRegex(final Pattern ptn, final TkRegex tgt) {
        this.pattern = ptn;
        this.target = tgt;
    }

    @Override
    public Iterator<Response> route(final Request req) throws IOException {
        final Matcher matcher = this.pattern.matcher(
            new RqHref.Base(req).href().path()
        );
        final Collection<Response> list = new ArrayList<Response>(1);
        if (matcher.matches()) {
            list.add(
                this.target.act(
                    new RqRegex() {
                        @Override
                        public Matcher matcher() {
                            return matcher;
                        }
                        @Override
                        public Iterable<String> head() throws IOException {
                            return req.head();
                        }
                        @Override
                        public InputStream body() throws IOException {
                            return req.body();
                        }
                    }
                )
            );
        }
        return list.iterator();
    }

}
