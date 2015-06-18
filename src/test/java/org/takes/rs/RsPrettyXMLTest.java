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
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RsPrettyXML}.
 * @author Igor Khvostenkov (ikhvostenkov@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class RsPrettyXMLTest {

    /**
     * RsPrettyXML can format response with XML body.
     * @throws IOException If some problem inside
     */
    @Test
    public void formatsXmlBody() throws IOException {
        MatcherAssert.assertThat(
            new RsPrint(
                new RsPrettyXML(
                    new RsWithBody("<test><a>foo</a></test>")
                )
            ).printBody(),
            Matchers.is("<test>\n   <a>foo</a>\n</test>\n")
        );
    }

    /**
     * RsPrettyXML retails the Doctype declaration when specified.
     * @throws IOException If some problem inside
     */
    @Test
    public void retainsDoctypeDeclaration() throws IOException {
        MatcherAssert.assertThat(
            new RsPrint(
                new RsPrettyXML(
                    new RsWithBody(
                        "<!DOCTYPE html><html><head></head><body></body></html>"
                    )
                )
            ).printBody(),
            Matchers.containsString("<!DOCTYPE HTML>")
        );
    }

    /**
     * RsPrettyXML can format response with non XML body.
     * @throws IOException If some problem inside
     */
    @Test(expected = IOException.class)
    public void formatsNonXmlBody() throws IOException {
        new RsPrint(new RsPrettyXML(new RsWithBody("foo"))).printBody();
    }

    /**
     * RsPrettyXML can report correct content length.
     * @throws IOException If some problem inside
     */
    @Test
    public void reportsCorrectContentLength() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new RsPrint(
            new RsWithBody(
                "<test>\n   <a>test</a>\n</test>\n"
            )
        ).printBody(baos);
        MatcherAssert.assertThat(
            new RsPrint(
                new RsPrettyXML(
                    new RsWithBody("<test><a>test</a></test>")
                )
            ).printHead(),
            Matchers.containsString(
                String.format(
                    "Content-Length: %d",
                    baos.toByteArray().length
                )
            )
        );
    }

    /**
     * RsPrettyXML can conform to equals and hash code contract.
     * @throws Exception If some problem inside
     */
    @Test
    public void conformsToEqualsAndHashCode() throws Exception {
        EqualsVerifier.forClass(RsPrettyXML.class)
            .suppress(Warning.TRANSIENT_FIELDS)
            .withRedefinedSuperclass()
            .verify();
    }
}
