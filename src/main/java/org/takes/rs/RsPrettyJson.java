/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
