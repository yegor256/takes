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
package org.takes.facets.auth;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.rq.RqHref;

/**
 * Passes by flag.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "flag", "passes" })
public final class PsByFlag implements Pass {

    /**
     * The flag.
     */
    private final transient String flag;

    /**
     * Flags and passes.
     */
    private final transient Map<String, Pass> passes;

    /**
     * Ctor.
     * @param pairs Map entries
     * @since 0.5.1
     */
    public PsByFlag(final PsByFlag.Pair... pairs) {
        this(PsByFlag.class.getSimpleName(), pairs);
    }

    /**
     * Ctor.
     * @param map Map
     */
    public PsByFlag(final Map<String, Pass> map) {
        this(PsByFlag.class.getSimpleName(), map);
    }

    /**
     * Ctor.
     * @param flg Flag
     * @param pairs Map entries
     * @since 0.5.1
     */
    public PsByFlag(final String flg, final PsByFlag.Pair... pairs) {
        this(flg, PsByFlag.asMap(pairs));
    }

    /**
     * Ctor.
     * @param flg Flag
     * @param map Map
     */
    public PsByFlag(final String flg, final Map<String, Pass> map) {
        this.flag = flg;
        this.passes = Collections.unmodifiableMap(map);
    }

    @Override
    public Iterator<Identity> enter(final Request req) throws IOException {
        final Iterator<String> flg = new RqHref(req).href()
            .param(this.flag).iterator();
        final Collection<Identity> users = new ArrayList<Identity>(1);
        if (flg.hasNext()) {
            final Pass pass = this.passes.get(flg.next());
            if (pass != null) {
                users.add(pass.enter(req).next());
            }
        }
        return users.iterator();
    }

    @Override
    public Response exit(final Response response,
        final Identity identity) {
        return response;
    }

    /**
     * Convert entries to map.
     * @param entries Entries
     * @return Map
     */
    private static Map<String, Pass> asMap(
        final Map.Entry<String, Pass>... entries) {
        final ConcurrentMap<String, Pass> map =
            new ConcurrentHashMap<String, Pass>(entries.length);
        for (final Map.Entry<String, Pass> ent : entries) {
            map.put(ent.getKey(), ent.getValue());
        }
        return map;
    }

    /**
     * Pair of values.
     */
    public static final class Pair
        extends AbstractMap.SimpleEntry<String, Pass> {
        /**
         * Serialization marker.
         */
        private static final long serialVersionUID = 7362482770166663015L;
        /**
         * Ctor.
         * @param key Key
         * @param pass Pass
         */
        public Pair(final String key, final Pass pass) {
            super(key, pass);
        }
    }

}
