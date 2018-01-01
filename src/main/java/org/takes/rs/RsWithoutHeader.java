/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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
package org.takes.rs;

import java.io.IOException;
import java.io.InputStream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;
import org.takes.misc.Condition;
import org.takes.misc.EnglishLowerCase;
import org.takes.misc.Select;

/**
 * Response decorator, without a header.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.9
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsWithoutHeader extends RsWrap {

    /**
     * Ctor.
     * @param res Original response
     * @param name Header name
     */
    public RsWithoutHeader(final Response res, final CharSequence name) {
        super(
            // @checkstyle AnonInnerLengthCheck (50 lines)
            new Response() {
                @Override
                public Iterable<String> head() throws IOException {
                    final String prefix = String.format(
                        "%s:", new EnglishLowerCase(name.toString()).string()
                    );
                    return new Select<String>(
                        res.head(),
                        new Condition<String>() {
                            @Override
                            public boolean fits(final String header) {
                                return !new EnglishLowerCase(header).string()
                                    .startsWith(prefix);
                            }
                        }
                    );
                }
                @Override
                public InputStream body() throws IOException {
                    return res.body();
                }
            }
        );
    }
}
