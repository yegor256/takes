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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import lombok.EqualsAndHashCode;
import org.takes.Response;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.InputSource;

/**
 * Response with properly indented XML body.
 *
 * <p>The class is immutable and thread-safe.
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
     * Response with properly transformed body.
     */
    private final transient List<Response> transformed =
        new CopyOnWriteArrayList<Response>();

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
    private Response make() throws IOException {
        synchronized (this.transformed) {
            if (this.transformed.isEmpty()) {
                this.transformed.add(
                    new RsWithBody(
                        this.origin,
                        RsPrettyXML.transform(this.origin.body())
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
     * @throws IOException If fails
     */
    private static byte[] transform(final InputStream body) throws IOException {
        final SAXSource source = new SAXSource(new InputSource(body));
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            final Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();
            // @checkstyle MultipleStringLiteralsCheck (2 line)
            transformer.setOutputProperty(
                OutputKeys.OMIT_XML_DECLARATION, "yes"
            );
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            if (isHtml5(source.getInputSource().getByteStream())) {
                // @checkstyle MultipleStringLiteralsCheck (1 line)
                transformer.setOutputProperty(OutputKeys.METHOD, "html");
                transformer.setOutputProperty(OutputKeys.VERSION, "5.0");
            }
            transformer.transform(source, new StreamResult(result));
        } catch (final TransformerException ex) {
            throw new IOException(ex);
        }
        return result.toByteArray();
    }

    /**
     * Checks if the input is an html5 document.
     * @param body The body to be checked.
     * @return True if the input is an html5 document.
     * @checkstyle MethodNameCheck (2 lines)
     */
    @SuppressWarnings({"PMD.AvoidCatchingGenericException",
        "PMD.OnlyOneReturn"})
    private static boolean isHtml5(final InputStream body) {
        final Document document;
        try {
            body.mark(Integer.MAX_VALUE);
            document = parseBody(body);
            body.reset();
            // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Exception ex) {
            return false;
        }
        final DocumentType doctype = document.getDoctype();
        return doctype != null
            && "html".equalsIgnoreCase(doctype.getName())
            && doctype.getSystemId() == null
            && doctype.getPublicId() == null;
    }

    /**
     * Parses the input stream and returns the Document built.
     * @param body The body to be parsed.
     * @return The document built.
     * @throws Exception if something goes wrong.
     */
    private static Document parseBody(final InputStream body) throws Exception {
        final DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(body);
    }
}
