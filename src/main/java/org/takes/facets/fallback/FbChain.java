/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
package org.takes.facets.fallback;

import java.util.Arrays;
import lombok.EqualsAndHashCode;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Fallback chain.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.13
 */
@EqualsAndHashCode(callSuper = true)
public final class FbChain extends FbWrap {

    /**
     * Ctor.
     * @param fallbacks Fallbacks to chain
     */
    public FbChain(final Fallback... fallbacks) {
        this(Arrays.asList(fallbacks));
    }

    /**
     * Ctor.
     * @param fallbacks Fallbacks
     */
    @SuppressWarnings
        (
            {
                "PMD.CallSuperInConstructor",
                "PMD.ConstructorOnlyInitializesOrCallOtherConstructors"
            }
        )
    public FbChain(final Iterable<Fallback> fallbacks) {
        super(
            req -> {
                Opt<Response> rsp = new Opt.Empty<>();
                for (final Fallback fbk : fallbacks) {
                    final Opt<Response> opt = fbk.route(req);
                    if (opt.has()) {
                        rsp = opt;
                        break;
                    }
                }
                return rsp;
            }
        );
    }

}
