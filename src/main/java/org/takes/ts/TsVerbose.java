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
import lombok.EqualsAndHashCode;
import org.takes.NotFoundException;
import org.takes.Request;
import org.takes.Take;
import org.takes.Takes;
import org.takes.rq.RqHref;
import org.takes.rq.RqMethod;

/**
 * Takes that makes all not-found exceptions location aware.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.10
 */
@EqualsAndHashCode(callSuper = true)
public final class TsVerbose extends TsWrap {

    /**
     * Ctor.
     * @param takes Original takes
     */
    public TsVerbose(final Takes takes) {
        super(
            new Takes() {
                @Override
                public Take route(final Request request) throws IOException {
                    try {
                        return takes.route(request);
                    } catch (final NotFoundException ex) {
                        throw new NotFoundException(
                            String.format(
                                "%s %s",
                                new RqMethod(request).method(),
                                new RqHref(request).href()
                            ),
                            ex
                        );
                    }
                }
            }
        );
    }

}
