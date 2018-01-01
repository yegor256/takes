/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;

/**
 * Response that converts Java object to JSON.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
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
            new RsJson.Source() {
                @Override
                public JsonStructure toJson() {
                    return json;
                }
            }
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
     * @throws IOException If fails
     */
    private static byte[] print(final RsJson.Source src) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonWriter writer = Json.createWriter(baos)) {
            writer.write(src.toJson());
        }
        return baos.toByteArray();
    }

    /**
     * Source with JSON.
     */
    public interface Source {
        /**
         * Get JSON value.
         * @return JSON
         * @throws IOException If fails
         */
        JsonStructure toJson() throws IOException;
    }

}
