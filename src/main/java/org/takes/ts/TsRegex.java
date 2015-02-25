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
package org.takes.ts;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Take;
import org.takes.Takes;
import org.takes.rq.RqQuery;
import org.takes.rq.RqRegex;
import org.takes.tk.TkText;

/**
 * Regex-based takes.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = "map")
public final class TsRegex implements Takes {

    /**
     * Patterns and their respective takes.
     */
    private final transient Map<Pattern, Takes> map;

    /**
     * Ctor.
     */
    public TsRegex() {
        this(Collections.<Pattern, Takes>emptyMap());
    }

    /**
     * Ctor.
     * @param tks Map of takes
     */
    public TsRegex(final Map<Pattern, Takes> tks) {
        this.map = Collections.unmodifiableMap(tks);
    }

    @Override
    public Take take(final Request request) throws IOException {
        final URI uri = new RqQuery(request).query();
        final String path = uri.getPath();
        Takes found = null;
        Matcher matcher = null;
        for (final Map.Entry<Pattern, Takes> ent : this.map.entrySet()) {
            matcher = ent.getKey().matcher(path);
            if (matcher.matches()) {
                found = ent.getValue();
                break;
            }
        }
        if (found == null) {
            throw new Takes.NotFoundException(
                String.format("nothing found for %s", path)
            );
        }
        return found.take(new TsRegex.Req(request, matcher));
    }

    /**
     * With this new take.
     * @param regex Regular expression
     * @param text Plain text content
     * @return New takes
     */
    public TsRegex with(final String regex, final String text) {
        return this.with(regex, new TkText(text));
    }

    /**
     * With this new take.
     * @param regex Regular expression
     * @param take The take
     * @return New takes
     */
    public TsRegex with(final String regex, final Take take) {
        return this.with(Pattern.compile(regex), take);
    }

    /**
     * With this new take.
     * @param regex Regular expression
     * @param take The take
     * @return New takes
     */
    public TsRegex with(final Pattern regex, final Take take) {
        return this.with(regex, new TsFixed(take));
    }

    /**
     * With this new take.
     * @param regex Regular expression
     * @param takes The takes
     * @return New takes
     */
    public TsRegex with(final Pattern regex, final Takes takes) {
        final ConcurrentMap<Pattern, Takes> tks =
            new ConcurrentHashMap<Pattern, Takes>(this.map.size() + 1);
        tks.putAll(this.map);
        tks.put(regex, takes);
        return new TsRegex(tks);
    }

    /**
     * Request with regex.
     */
    private static final class Req implements RqRegex {
        /**
         * Original.
         */
        private final transient Request origin;
        /**
         * Matcher.
         */
        private final transient Matcher mtr;
        /**
         * Ctor.
         * @param req Request
         * @param matcher Matcher
         */
        private Req(final Request req, final Matcher matcher) {
            this.origin = req;
            this.mtr = matcher;
        }
        @Override
        public Matcher matcher() {
            return this.mtr;
        }
        @Override
        public List<String> head() {
            return this.origin.head();
        }
        @Override
        public InputStream body() {
            return this.origin.body();
        }
    }

}
