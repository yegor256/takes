/*
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
 * A take decorator that joins multiple Set-Cookie headers into a single header.
 *
 * <p>This decorator combines multiple Set-Cookie headers into one comma-separated
 * header as specified in RFC 6265. However, this approach may not be compatible
 * with all browsers, as most expect separate Set-Cookie headers for each cookie.
 * This class should be used only in specific cases where joined cookies are required.
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
     * Constructor that wraps a take to join Set-Cookie headers.
     * @param take The take to wrap with cookie joining functionality
     * @checkstyle AnonInnerLengthCheck (100 lines)
     */
    public TkJoinedCookies(final Take take) {
        super(
            req -> TkJoinedCookies.join(take.act(req))
        );
    }

    /**
     * Joins multiple Set-Cookie headers into a single comma-separated header.
     * @param response The response containing Set-Cookie headers
     * @return A new response with joined Set-Cookie headers
     * @throws Exception If response processing fails
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
