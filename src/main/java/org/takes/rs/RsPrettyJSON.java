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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import lombok.EqualsAndHashCode;
import org.takes.Response;

/**
 * Response with properly indented JSON body.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Eugene Kondrashev (eugene.kondrashev@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@EqualsAndHashCode(of = "origin")
public final class RsPrettyJSON implements Response {

    /**
     * Original response.
     */
    private final transient Response origin;

    /**
     * Response with properly transformed body.
     */
    private final transient List<Response> transformed =
        new CopyOnWriteArrayList<Response>();

    /**
     * Ctor.
     * @param res Original response
     */
    public RsPrettyJSON(final Response res) {
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
     * @throws java.io.IOException If fails
     */
    private Response make() throws IOException {
        synchronized (this.transformed) {
            if (this.transformed.isEmpty()) {
                this.transformed.add(
                    new RsWithBody(
                        this.origin,
                        RsPrettyJSON.transform(this.origin.body())
                    )
                );
            }
        }
        return this.transformed.get(0);
    }

    /**
     * Format body with proper indents.
     * @param body Response body
     * @return New properly formatted body
     * @throws java.io.IOException If fails
     */
    private static byte[] transform(final InputStream body) throws IOException {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final JsonReader reader = Json.createReader(body);
        final Map<String, ?> props = Collections.singletonMap(
            JsonGenerator.PRETTY_PRINTING,
            true
        );
        final JsonWriterFactory factory = Json.createWriterFactory(props);
        final JsonWriter writer = factory.createWriter(result);
        try {
            final JsonObject obj = reader.readObject();
            writer.writeObject(obj);
        } catch (final JsonException ex) {
            throw new IOException(ex);
        } finally {
            reader.close();
            writer.close();
        }
        return result.toByteArray();
    }
}
