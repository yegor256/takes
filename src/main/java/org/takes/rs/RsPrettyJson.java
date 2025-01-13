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
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonWriter;
import jakarta.json.stream.JsonGenerator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;

/**
 * Response with properly indented JSON body.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 1.0
 */
@ToString(of = "origin")
@EqualsAndHashCode
public final class RsPrettyJson implements Response {

    /**
     * Original response.
     */
    private final Response origin;

    /**
     * Response with properly transformed body.
     */
    private final List<Response> transformed;

    /**
     * Ctor.
     * @param res Original response
     */
    public RsPrettyJson(final Response res) {
        this.transformed = new CopyOnWriteArrayList<>();
        this.origin = res;
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.make().head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.make().body();
    }

    /**
     * Make a response.
     * @return Response just made
     * @throws IOException If fails
     */
    private Response make() throws IOException {
        if (this.transformed.isEmpty()) {
            this.transformed.add(
                new RsWithBody(
                    this.origin,
                    RsPrettyJson.transform(this.origin.body())
                )
            );
        }
        return this.transformed.get(0);
    }

    /**
     * Format body with proper indents.
     * @param body Response body
     * @return New properly formatted body
     * @throws IOException If fails
     */
    private static byte[] transform(final InputStream body) throws IOException {
        final ByteArrayOutputStream res = new ByteArrayOutputStream();
        try (JsonReader rdr = Json.createReader(body)) {
            final JsonObject obj = rdr.readObject();
            try (JsonWriter wrt = Json.createWriterFactory(
                Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true)
            )
                .createWriter(res)
            ) {
                wrt.writeObject(obj);
            }
        } catch (final JsonException ex) {
            throw new IOException(ex);
        }
        return res.toByteArray();
    }
}
