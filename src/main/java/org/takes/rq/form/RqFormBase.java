/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.form;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.EqualsAndHashCode;
import org.cactoos.Text;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Lowered;
import org.cactoos.text.Split;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;
import org.cactoos.text.UncheckedText;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.misc.VerboseIterable;
import org.takes.rq.RqForm;
import org.takes.rq.RqPrint;
import org.takes.rq.RqWrap;

/**
 * Base implementation of {@link RqForm}.
 * @since 0.33
 */
@EqualsAndHashCode(callSuper = true)
public final class RqFormBase extends RqWrap implements RqForm {

    /**
     * Request.
     */
    private final Request req;

    /**
     * Saved map.
     */
    private final List<Map<String, List<String>>> saved;

    /**
     * Ctor.
     * @param request Original request
     */
    public RqFormBase(final Request request) {
        super(request);
        this.saved = new CopyOnWriteArrayList<>();
        this.req = request;
    }

    @Override
    public Iterable<String> param(final CharSequence key) throws IOException {
        final List<String> values = this.map().getOrDefault(
            new UncheckedText(
                new Lowered(key.toString())
            ).asString(),
            Collections.emptyList()
        );
        final Iterable<String> iter;
        if (values.isEmpty()) {
            iter = new VerboseIterable<>(
                Collections.emptyList(),
                new FormattedText(
                    "there are no params \"%s\" among %d others: %s",
                    key, this.map().size(), this.map().keySet()
                )
            );
        } else {
            iter = new VerboseIterable<>(
                values,
                new FormattedText(
                    "there are only %d params by name \"%s\"",
                    values.size(), key
                )
            );
        }
        return iter;
    }

    @Override
    public Iterable<String> names() throws IOException {
        return this.map().keySet();
    }

    /**
     * Decode from URL.
     * @param txt Text
     * @return Decoded
     */
    private static String decode(final Text txt) {
        return URLDecoder.decode(
            new UncheckedText(txt).asString(),
            Charset.defaultCharset()
        );
    }

    /**
     * Create map of request parameters.
     * @return Parameters map or empty map in case of error
     * @throws IOException If something fails reading or parsing body
     */
    private Map<String, List<String>> map() throws IOException {
        if (this.saved.isEmpty()) {
            this.saved.add(this.freshMap());
        }
        return this.saved.get(0);
    }

    /**
     * Create map of request parameter.
     * @return Parameters map or empty map in case of error
     * @throws IOException If something fails reading or parsing body
     */
    private Map<String, List<String>> freshMap() throws IOException {
        final String body = new RqPrint(this.req).printBody();
        final Map<String, List<String>> map = new HashMap<>(1);
        for (final Text txt : new Split(body, "&")) {
            final String pair = new UncheckedText(txt).asString();
            if (pair.isEmpty()) {
                continue;
            }
            final String[] parts = pair.split("=", 2);
            if (parts.length < 2) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    String.format(
                        "invalid form body pair: %s", pair
                    )
                );
            }
            final String key = RqFormBase.decode(new Lowered(parts[0]));
            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>(0));
            }
            map.get(key).add(
                RqFormBase.decode(
                    new Trimmed(new TextOf(parts[1]))
                )
            );
        }
        return map;
    }
}
