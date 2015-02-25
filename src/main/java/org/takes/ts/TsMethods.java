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
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Take;
import org.takes.Takes;
import org.takes.rq.RqMethod;

/**
 * Method-based takes.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = "map")
public final class TsMethods implements Takes {

    /**
     * Method names and their respective takes.
     */
    private final transient Map<String, Takes> map;

    /**
     * Ctor.
     */
    public TsMethods() {
        this(Collections.<String, Takes>emptyMap());
    }

    /**
     * Ctor.
     * @param tks Map of takes
     */
    public TsMethods(final Map<String, Takes> tks) {
        this.map = Collections.unmodifiableMap(tks);
    }

    @Override
    public Take take(final Request request) throws IOException {
        final String method = new RqMethod(request).method();
        Takes found = null;
        for (final Map.Entry<String, Takes> ent : this.map.entrySet()) {
            if (method.equals(ent.getKey())) {
                found = ent.getValue();
                break;
            }
        }
        if (found == null) {
            throw new Takes.NotFoundException(
                String.format("method %s not allowed", method)
            );
        }
        return found.take(request);
    }

    /**
     * With this new take.
     * @param mtd Method
     * @param take The take
     * @return New takes
     */
    public TsMethods with(final String mtd, final Take take) {
        return this.with(mtd, new TsFixed(take));
    }

    /**
     * With this new take.
     * @param mtd Method
     * @param takes The takes
     * @return New takes
     */
    public TsMethods with(final String mtd, final Takes takes) {
        final ConcurrentMap<String, Takes> tks =
            new ConcurrentHashMap<String, Takes>(this.map.size() + 1);
        tks.putAll(this.map);
        tks.put(mtd, takes);
        return new TsMethods(tks);
    }

}
