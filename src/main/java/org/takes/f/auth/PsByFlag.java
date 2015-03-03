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
package org.takes.f.auth;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.rq.RqQuery;

/**
 * Passes by flag.
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
     * @param map Map
     */
    public PsByFlag(final Map<String, Pass> map) {
        this(PsByFlag.class.getSimpleName(), map);
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
    public Identity enter(final Request request) throws IOException {
        final List<String> flg = new RqQuery(request).param(this.flag);
        final Identity identity;
        if (flg.isEmpty()) {
            identity = Identity.ANONYMOUS;
        } else {
            identity = this.passes.get(flg.get(0)).enter(request);
        }
        return identity;
    }

    @Override
    public Response exit(final Response response,
        final Identity identity) {
        return response;
    }

}
