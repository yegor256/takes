/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2024 Yegor Bugayenko
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

import java.io.IOException;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import org.cactoos.Text;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;
import org.cactoos.text.UncheckedText;
import org.takes.Request;
import org.takes.misc.Href;

/**
 * Request decorator, for HTTP URI query parsing.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.13.1
 */
@EqualsAndHashCode(callSuper = true)
public final class RqHrefBase extends RqWrap implements RqHref {
    /**
     * Ctor.
     * @param req Original request
     */
    public RqHrefBase(final Request req) {
        super(req);
    }

    @Override
    public Href href() throws IOException {
        final String uri = new RqRequestLine.Base(this).uri();
        final Iterator<String> hosts = new RqHeaders.Base(this)
            .header("host").iterator();
        final Iterator<String> protos = new RqHeaders.Base(this)
            .header("x-forwarded-proto").iterator();
        final Text host;
        if (hosts.hasNext()) {
            host = new Trimmed(new TextOf(hosts.next()));
        } else {
            host = new TextOf("localhost");
        }
        final Text proto;
        if (protos.hasNext()) {
            proto = new Trimmed(new TextOf(protos.next()));
        } else {
            proto = new TextOf("http");
        }
        return new Href(
            String.format(
                "%s://%s%s",
                new UncheckedText(proto).asString(),
                new UncheckedText(host).asString(),
                uri
            )
        );
    }
}
