/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.cookies;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsWithHeader;
import org.takes.rs.RsWithoutHeader;
import org.takes.tk.TkWrap;

/**
 * Set-Cookie headers will be joined.
 *
 * <p>This may be useful for some browsers. When you drop a few cookies,
 * the RFC-6265 requires them to be joined in one Set-Cookie header. However,
 * most browsers don't support that. They want to see separate
 * headers. This class may be useful, but in some very specific cases.</p>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.11
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkJoinedCookies extends TkWrap {

    /**
     * Pattern to find them.
     */
    private static final Pattern PTN = Pattern.compile(
        "set-cookie: (.+)", Pattern.CASE_INSENSITIVE
    );

    /**
     * Ctor.
     * @param take The take to wrap
     * @checkstyle AnonInnerLengthCheck (100 lines)
     */
    public TkJoinedCookies(final Take take) {
        super(
            req -> TkJoinedCookies.join(take.act(req))
        );
    }

    /**
     * Join them.
     * @param response The response
     * @return New response
     * @throws Exception If fails
     */
    private static Response join(final Response response) throws Exception {
        final StringBuilder cookies = new StringBuilder();
        for (final String header : response.head()) {
            final Matcher matcher =
                TkJoinedCookies.PTN.matcher(header);
            if (!matcher.matches()) {
                continue;
            }
            cookies.append(matcher.group(1)).append(", ");
        }
        final Response out;
        if (cookies.length() > 0) {
            out = new RsWithHeader(
                new RsWithoutHeader(response, "Set-cookie"),
                "Set-Cookie", cookies.toString()
            );
        } else {
            out = response;
        }
        return out;
    }

}
