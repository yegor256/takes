/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;
import org.takes.HttpException;

/**
 * Request target encoding.
 * @since 2.0
 */
final class Target {

    /**
     * Utility class.
     */
    private Target() {
        // intentionally empty
    }

    static String encoded(final String value) throws IOException {
        final StringBuilder uri = new StringBuilder(value);
        while (true) {
            try {
                return new URI(uri.toString()).toASCIIString();
            } catch (final URISyntaxException err) {
                final int index = err.getIndex();
                if (
                    index < 0 || index >= uri.length()
                        || !Target.query(uri, index)
                ) {
                    throw new HttpException(
                        HttpURLConnection.HTTP_BAD_REQUEST,
                        new UncheckedText(
                            new FormattedText(
                                RqRequestLine.Base.BAD_REQUEST_MSG,
                                value
                            )
                        ).asString(),
                        err
                    );
                }
                final int point = uri.codePointAt(index);
                uri.replace(
                    index,
                    index + Character.charCount(point),
                    Target.encode(point)
                );
            }
        }
    }

    private static boolean query(final StringBuilder uri, final int index) {
        final int start = uri.indexOf("?");
        return start >= 0 && index > start;
    }

    private static String encode(final int point) {
        final StringBuilder text = new StringBuilder();
        for (final byte octet : new String(
            Character.toChars(point)
        ).getBytes(StandardCharsets.UTF_8)) {
            text.append('%').append(
                new UncheckedText(new FormattedText("%02X", octet & 0xFF)).asString()
            );
        }
        return text.toString();
    }
}
