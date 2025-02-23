/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.HttpException;
import org.takes.Take;
import org.takes.rq.RqFake;

/**
 * Test case for {@link TkVerbose}.
 * @since 0.10
 */
final class TkVerboseTest {

    @Test
    void extendsNotFoundException() throws Exception {
        final Take take = request -> {
            throw new HttpException(HttpURLConnection.HTTP_NOT_FOUND);
        };
        try {
            new TkVerbose(take).act(new RqFake());
        } catch (final HttpException ex) {
            MatcherAssert.assertThat(
                ex.getLocalizedMessage(),
                Matchers.endsWith("GET http://www.example.com/")
            );
        }
    }

}
