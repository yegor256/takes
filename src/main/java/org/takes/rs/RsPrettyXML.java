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

import com.jcabi.aspects.Cacheable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import lombok.EqualsAndHashCode;
import org.takes.Response;

/**
 * Response with properly indented XML body.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Igor Khvostenkov (ikhvostenkov@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@EqualsAndHashCode(of = "origin")
public final class RsPrettyXML implements Response {

    /**
     * Original response.
     */
    private final transient Response origin;

    /**
     * Ctor.
     * @param res Original response
     */
    public RsPrettyXML(final Response res) {
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
    @Cacheable
    private Response make() throws IOException {
        return new RsWithBody(
            this.origin,
            RsPrettyXML.transform(this.origin.body())
        );
    }

    /**
     * Format body with proper indents.
     * @param body Response body
     * @return New properly formatted body
     * @throws IOException If fails
     */
    private static byte[] transform(final InputStream body) throws IOException {
        final StreamSource source = new StreamSource(body);
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            final Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();
            // @checkstyle MultipleStringLiteralsCheck (2 line)
            transformer.setOutputProperty(
                OutputKeys.OMIT_XML_DECLARATION, "yes"
            );
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, new StreamResult(result));
        } catch (final TransformerException ex) {
            throw new IOException(ex);
        }
        return result.toByteArray();
    }
}
