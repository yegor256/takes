/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Yegor Bugayenko
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
package org.takes.rq;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.cactoos.io.BytesOf;
import org.cactoos.io.UncheckedBytes;

/**
 * Fake request (for unit tests).
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class RqFake extends RqWrap {

    /**
     * Ctor.
     */
    public RqFake() {
        this("GET");
    }

    /**
     * Ctor.
     * @param method HTTP method
     */
    public RqFake(final CharSequence method) {
        this(method, "/ HTTP/1.1 ");
    }

    /**
     * Ctor.
     * @param method HTTP method
     * @param query HTTP query
     */
    public RqFake(final CharSequence method, final CharSequence query) {
        this(method, query, "");
    }

    /**
     * Ctor.
     * @param method HTTP method
     * @param query HTTP query
     * @param body HTTP body
     */
    public RqFake(final CharSequence method, final CharSequence query,
        final CharSequence body) {
        this(
            Arrays.asList(
                String.format("%s %s", method, query),
                "Host: www.example.com"
            ),
            body
        );
    }

    /**
     * Ctor.
     * @param head Head
     * @param body Body
     */
    public RqFake(final List<String> head, final CharSequence body) {
        this(
            head,
            new UncheckedBytes(
                new BytesOf(body.toString())
            ).asBytes());
    }

    /**
     * Ctor.
     * @param head Head
     * @param body Body
     */
    public RqFake(final List<String> head, final byte[] body) {
        this(
            head,
            new ByteArrayInputStream(Arrays.copyOf(body, body.length))
        );
    }

    /**
     * Ctor.
     * @param head Head
     * @param body Body
     */
    public RqFake(final List<String> head, final InputStream body) {
        super(new RequestOf(Collections.unmodifiableList(head), body));
    }
}
