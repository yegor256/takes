/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonStructure;
import lombok.EqualsAndHashCode;
import org.takes.Response;

/**
 * Response that converts Java object to JSON.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = "source")
public final class RsJSON implements Response {

    /**
     * JSON source.
     */
    private final transient RsJSON.Source source;

    /**
     * Ctor.
     * @param json JSON object
     */
    public RsJSON(final JsonStructure json) {
        this(
            new RsJSON.Source() {
                @Override
                public JsonStructure toJSON() {
                    return json;
                }
            }
        );
    }

    /**
     * Ctor.
     * @param src Source
     */
    public RsJSON(final RsJSON.Source src) {
        this.source = src;
    }

    @Override
    public Iterable<String> head() throws IOException {
        return new RsWithType(
            new RsWithStatus(new RsEmpty(), HttpURLConnection.HTTP_OK),
            "application/json"
        ).head();
    }

    @Override
    public InputStream body() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Json.createWriter(baos).write(this.source.toJSON());
        return new ByteArrayInputStream(baos.toByteArray());
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
        JsonStructure toJSON() throws IOException;
    }

}
