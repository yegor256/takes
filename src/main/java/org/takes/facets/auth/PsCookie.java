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
package org.takes.facets.auth;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.text.TextOf;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.auth.codecs.Codec;
import org.takes.facets.cookies.RqCookies;
import org.takes.facets.cookies.RsWithCookie;
import org.takes.misc.Expires;
import org.takes.misc.Opt;

/**
 * Pass via cookie information.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode
public final class PsCookie implements Pass {

    /**
     * Codec.
     */
    private final Codec codec;

    /**
     * Cookie to read.
     */
    private final String cookie;

    /**
     * Max login age, in days.
     */
    private final long age;

    /**
     * Ctor.
     * @param cdc Codec
     */
    public PsCookie(final Codec cdc) {
        this(cdc, PsCookie.class.getSimpleName());
    }

    /**
     * Ctor.
     * @param cdc Codec
     * @param name Cookie name
     */
    public PsCookie(final Codec cdc, final String name) {
        this(cdc, name, 30L);
    }

    /**
     * Ctor.
     * @param cdc Codec
     * @param name Cookie name
     * @param days Max age in days
     * @since 0.9.6
     */
    public PsCookie(final Codec cdc, final String name, final long days) {
        this.codec = cdc;
        this.cookie = name;
        this.age = days;
    }

    @Override
    public Opt<Identity> enter(final Request req) throws IOException {
        final Iterator<String> cookies = new RqCookies.Base(req)
            .cookie(this.cookie).iterator();
        Opt<Identity> user = new Opt.Empty<>();
        if (cookies.hasNext()) {
            user = new Opt.Single<>(
                this.codec.decode(
                    new UncheckedBytes(
                        new BytesOf(cookies.next())
                    ).asBytes()
                )
            );
        }
        return user;
    }

    @Override
    public Response exit(final Response res,
        final Identity idt) throws IOException {
        final String text;
        if (idt.equals(Identity.ANONYMOUS)) {
            text = "";
        } else {
            text = new TextOf(this.codec.encode(idt)).toString();
        }
        return new RsWithCookie(
            res, this.cookie, text,
            "Path=/",
            "HttpOnly",
            new Expires.Date(
                System.currentTimeMillis()
                    + TimeUnit.DAYS.toMillis(this.age)
            ).print()
        );
    }
}
