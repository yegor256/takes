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
