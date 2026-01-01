/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.cactoos.text.Lowered;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;
import org.cactoos.text.UncheckedText;
import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;
import org.takes.rq.RqHeaders;

/**
 * Fork by encoding accepted by "Accept-Encoding" HTTP header.
 *
 * <p>Use this fork in order to deliver responses with different
 * encoding, depending on user preferences. For example, you want
 * to deliver GZIP-compressed response when "Accept-Encoding" request
 * header contains "gzip". Here is how:
 *
 * <pre> new TkFork(
 *   new FkEncoding("gzip", new RsGzip(response)),
 *   new FkEncoding("", response)
 * )</pre>
 *
 * <p>Empty string as an encoding means that the fork should match
 * in any case.
 *
 * <p>The class is immutable and thread-safe.
 * @see org.takes.facets.fork.RsFork
 * @since 0.10
 */
@EqualsAndHashCode
public final class FkEncoding implements Fork {

    /**
     * Accept-Encoding separator.
     */
    private static final Pattern ENCODING_SEP = Pattern.compile("\\s*,\\s*");

    /**
     * Encoding we can deliver (or empty string).
     */
    private final String encoding;

    /**
     * Response to return.
     */
    private final Response origin;

    /**
     * Ctor.
     * @param enc Encoding we accept
     * @param response Response to return
     */
    public FkEncoding(final String enc, final Response response) {
        this.encoding = new UncheckedText(
            new Lowered(new Trimmed(new TextOf(enc)))
        ).asString();
        this.origin = response;
    }

    @Override
    public Opt<Response> route(final Request req) throws IOException {
        final Iterator<String> headers =
            new RqHeaders.Base(req).header("Accept-Encoding").iterator();
        final Opt<Response> resp;
        if (this.encoding.isEmpty()) {
            resp = new Opt.Single<>(this.origin);
        } else if (headers.hasNext()) {
            final Collection<String> items = Arrays.asList(
                FkEncoding.ENCODING_SEP.split(
                    new UncheckedText(
                        new Lowered(
                            new Trimmed(
                                new TextOf(headers.next())
                            )
                        )
                    ).asString()
                )
            );
            if (items.contains(this.encoding)) {
                resp = new Opt.Single<>(this.origin);
            } else {
                resp = new Opt.Empty<>();
            }
        } else {
            resp = new Opt.Empty<>();
        }
        return resp;
    }

}
