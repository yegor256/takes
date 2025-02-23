/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
