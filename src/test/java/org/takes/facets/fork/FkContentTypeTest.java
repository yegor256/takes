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
package org.takes.facets.fork;

import java.io.IOException;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsEmpty;

/**
 * Test case for {@link FkContentType}.
 * @author Igor Khvostenkov (ikhvostenkov@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
public final class FkContentTypeTest {

    /**
     * FkContentType can match by Content-Type header.
     * @throws IOException If some problem inside
     */
    @Test
    public void matchesByContentTypeHeader() throws IOException {
        final String contenttype = "Content-Type";
        MatcherAssert.assertThat(
            new FkContentType("text/xml", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), contenttype, "*/* ")
            ).has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new FkContentType(
                "application/json charset=utf-8", new RsEmpty()
            ).route(
                new RqWithHeader(new RqFake(), contenttype, "image/*")
            ).has(),
            Matchers.is(false)
        );
        MatcherAssert.assertThat(
            new FkContentType(
                "text/html charset=iso-8859-1", new RsEmpty()
            ).route(
                new RqWithHeader(
                    // @checkstyle MultipleStringLiteralsCheck (1 line)
                    new RqFake(), contenttype, "text/html charset=iso-8859-1"
                )
            ).has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new FkContentType("*/*", new RsEmpty()).route(
                new RqWithHeader(new RqFake(), contenttype, "")
            ).has(),
            Matchers.is(true)
        );
    }

    /**
     * Checks FkContentType equals method.
     * @throws Exception If some problem inside
     */
    @Test
    public void equalsAndHashCodeEqualTest() throws Exception {
        EqualsVerifier.forClass(FkContentType.class)
            .suppress(Warning.TRANSIENT_FIELDS)
            .verify();
    }
}
