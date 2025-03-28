/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonStructure;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RsJson}.
 * @since 0.1
 */
final class RsJsonTest {

    @Test
    void buildsJsonResponse() throws IOException {
        final String key = "name";
        final JsonStructure json = Json.createObjectBuilder()
            .add(key, "Jeffrey Lebowski")
            .build();
        MatcherAssert.assertThat(
            Json.createReader(
                new RsJson(json).body()
            ).readObject().getString(key),
            Matchers.startsWith("Jeffrey")
        );
    }

    @Test
    void buildsBigJsonResponse() throws IOException {
        final int size = 100_000;
        final JsonArrayBuilder builder = Json.createArrayBuilder();
        for (int idx = 0; idx < size; ++idx) {
            builder.add(
                Json.createObjectBuilder().add("number", "212 555-1234")
            );
        }
        MatcherAssert.assertThat(
            Json.createReader(
                new RsJson(builder.build()).body()
            ).readArray().size(),
            Matchers.equalTo(size)
        );
    }

}
