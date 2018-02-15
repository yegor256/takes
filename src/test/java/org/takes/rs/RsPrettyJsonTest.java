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
import nl.jqno.equalsverifier.EqualsVerifier;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RsPrettyJson}.
 * @author Eugene Kondrashev (eugene.kondrashev@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class RsPrettyJsonTest {

    /**
     * RsPrettyJSON can format response with JSON body.
     * @throws Exception If some problem inside
     */
    @Test
    public void formatsJsonBody() throws Exception {
        MatcherAssert.assertThat(
            new RsPrint(
                new RsPrettyJson(
                    new RsWithBody("{\"widget\": {\"debug\": \"on\" }}")
                )
            ).printBody(),
            Matchers.is(
                "\n{\n    \"widget\":{\n        \"debug\":\"on\"\n    }\n}"
            )
        );
    }

    /**
     * RsPrettyJSON can reject a non-JSON body.
     * @throws Exception If some problem inside
     */
    @Test(expected = IOException.class)
    public void rejectsNonJsonBody() throws Exception {
        new RsPrint(new RsPrettyJson(new RsWithBody("foo"))).printBody();
    }

    /**
     * RsPrettyJSON can report correct content length.
     * @throws Exception If some problem inside
     */
    @Test
    public void reportsCorrectContentLength() throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new RsPrint(
            new RsWithBody(
                "\n{\n    \"test\":{\n        \"test\":\"test\"\n    }\n}"
            )
        ).printBody(baos);
        MatcherAssert.assertThat(
            new RsPrint(
                new RsPrettyJson(
                    new RsWithBody("{\"test\": {\"test\": \"test\" }}")
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
     * RsPrettyJSON can conform to equals and hash code contract.
     * @throws Exception If some problem inside
     */
    @Test
    public void conformsToEqualsAndHashCode() throws Exception {
        EqualsVerifier.forClass(RsPrettyJson.class)
            .withRedefinedSuperclass()
            .verify();
    }
}
