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
package org.takes.facets.fork;

import java.io.IOException;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
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
     * Content-Type header.
     */
    private static final String CONTENT_TYPE = "Content-Type";

    /**
     * Content-Type value.
     */
    private static final String CTYPE = "text/html charset=iso-8859-1";

    /**
     * FkContentType can match by Content-Type header with any of types.
     * @throws IOException If some problem inside
     */
    @Test
    public void matchesWithAnyTypes() throws IOException {
        MatcherAssert.assertThat(
            new FkContentType("text/xml", new RsEmpty()).route(
                new RqWithHeader(
                    new RqFake(),
                    FkContentTypeTest.CONTENT_TYPE,
                    "*/* "
                )
            ).has(),
            Matchers.is(true)
        );
    }

    /**
     * FkContentType can match by Content-Type header with different types.
     * @throws IOException If some problem inside
     */
    @Test
    public void matchesDifferentTypes() throws IOException {
        MatcherAssert.assertThat(
            new FkContentType(
                "application/json charset=utf-8",
                FkContentTypeTest.emptyResponse()
            ).route(
                new RqWithHeader(
                    new RqFake(),
                    FkContentTypeTest.CONTENT_TYPE,
                    "images/*"
                )
            ).has(),
            Matchers.is(false)
        );
    }

    /**
     * FkContentType can match by Content-Type header with identical types.
     * @throws IOException If some problem inside
     */
    @Test
    public void matchesIdenticalTypes() throws IOException {
        MatcherAssert.assertThat(
            new FkContentType(
                FkContentTypeTest.CTYPE,
                FkContentTypeTest.emptyResponse()
            ).route(
                new RqWithHeader(
                    new RqFake(),
                    FkContentTypeTest.CONTENT_TYPE,
                    FkContentTypeTest.CTYPE
                )
            ).has(),
            Matchers.is(true)
        );
    }

    /**
     * FkContentType can match by Content-Type header with empty type.
     * @throws IOException If some problem inside
     */
    @Test
    public void matchesEmptyType() throws IOException {
        MatcherAssert.assertThat(
            new FkContentType(
                "*/*",
                FkContentTypeTest.emptyResponse()
            ).route(
                new RqWithHeader(
                    new RqFake(), FkContentTypeTest.CONTENT_TYPE, ""
                )
            ).has(), Matchers.is(true)
        );
    }

    /**
     * FkContentType can match by Content-Type header with different encodings.
     * @throws IOException If some problem inside
     */
    @Test
    public void matchesDifferentEncodingsTypes() throws IOException {
        MatcherAssert.assertThat(
            new FkContentType(
                FkContentTypeTest.CTYPE,
                FkContentTypeTest.emptyResponse()
            ).route(
                new RqWithHeader(
                    new RqFake(),
                    FkContentTypeTest.CONTENT_TYPE,
                    "text/html charset=utf8"
                )
            ).has(),
            Matchers.is(false)
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

    /**
     * Create a Take instance with empty response.
     * @return Take
     */
    private static Take emptyResponse() {
        return new Take() {
            @Override
            public Response act(final Request req) throws IOException {
                return new RsEmpty();
            }
        };
    }
}
