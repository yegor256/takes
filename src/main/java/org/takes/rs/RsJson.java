/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs;

import jakarta.json.Json;
import jakarta.json.JsonStructure;
import jakarta.json.JsonWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;

/**
 * Response decorator that creates JSON responses from Java objects.
 *
 * <p>This decorator converts Java objects to JSON format using Jakarta JSON API
 * and automatically sets the content type to "application/json". It accepts
 * JsonStructure objects or Source implementations that provide JSON content.
 * The serialization is performed using standard JSON writers.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsJson extends RsWrap {

    /**
     * Ctor.
     * @param json JSON object
     * @throws IOException If fails
     */
    public RsJson(final JsonStructure json) throws IOException {
        this(
            () -> json
        );
    }

    /**
     * Ctor.
     * @param src Source
     * @throws IOException If fails
     */
    public RsJson(final RsJson.Source src) throws IOException {
        this(new RsWithBody(RsJson.print(src)));
    }

    /**
     * Ctor.
     * @param res Resource
     */
    public RsJson(final Response res) {
        super(
            new RsWithType(
                new RsWithStatus(res, HttpURLConnection.HTTP_OK),
                "application/json"
        )
        );
    }

    /**
     * Print JSON.
     * @param src Source
     * @return JSON
     */
    private static byte[] print(final RsJson.Source src) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonWriter writer = Json.createWriter(baos)) {
            writer.write(src.toJson());
        }
        return baos.toByteArray();
    }

    /**
     * Source with JSON.
     * @since 0.1
     */
    public interface Source {
        /**
         * Get JSON value.
         * @return JSON
         */
        JsonStructure toJson();
    }

}
