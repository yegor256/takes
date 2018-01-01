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
package org.takes.rq.multipart;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.takes.rq.RqFake;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqMultipart;
import org.takes.rq.RqPrint;
import org.takes.rq.RqWithHeader;
import org.takes.rq.RqWithHeaders;

/**
 * Test case for {@link RqMtFake}.
 * @author Nicolas Filotto (nicolas.filotto@gmail.com)
 * @version $Id$
 * @since 0.33
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
public final class RqMtFakeTest {
    /**
     * Form data.
     */
    private static final String FORM_DATA =
        "form-data; name=\"data\"; filename=\"%s\"";
    /**
     * Content disposition.
     */
    private static final String DISPOSITION = "Content-Disposition";
    /**
     * RqMtFake can throw exception on no name
     * at Content-Disposition header.
     * @throws IOException if some problem inside
     */
    @Test(expected = IOException.class)
    public void throwsExceptionOnNoNameAtContentDispositionHeader()
        throws IOException {
        new RqMtFake(
            new RqWithHeader(
                new RqFake("", "", "340 N Wolfe Rd, Sunnyvale, CA 94085"),
                RqMtFakeTest.DISPOSITION, "form-data; fake=\"t-3\""
            )
        );
    }

    /**
     * RqMtFake can throw exception on no boundary
     * at Content-Type header.
     * @throws IOException if some problem inside
     */
    @Test(expected = IOException.class)
    public void throwsExceptionOnNoBoundaryAtContentTypeHeader()
        throws IOException {
        new RqMtBase(
            new RqFake(
                Arrays.asList(
                    "POST /h?s=3 HTTP/1.1",
                    "Host: wwo.example.com",
                    "Content-Type: multipart/form-data; boundaryAaB03x",
                    "Content-Length: 100005"
                ),
                ""
            )
        );
    }

    /**
     * RqMtFake can throw exception on invalid Content-Type header.
     * @throws IOException if some problem inside
     */
    @Test(expected = IOException.class)
    public void throwsExceptionOnInvalidContentTypeHeader() throws IOException {
        new RqMtBase(
            new RqFake(
                Arrays.asList(
                    "POST /h?r=3 HTTP/1.1",
                    "Host: www.example.com",
                    "Content-Type: multipart; boundary=AaB03x",
                    "Content-Length: 100004"
                ),
                ""
            )
        );
    }

    /**
     * RqMtFake can parse http body.
     * @throws IOException If some problem inside
     */
    @Test
    public void parsesHttpBody() throws IOException {
        final String body = "40 N Wolfe Rd, Sunnyvale, CA 94085";
        final String part = "t4";
        final RqMultipart multi = new RqMtFake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                RqMtFakeTest.contentLengthHeader(
                    (long) body.getBytes().length
                ),
                RqMtFakeTest.contentDispositionHeader(
                    String.format("form-data; name=\"%s\"", part)
                )
            ),
            new RqWithHeaders(
                new RqFake("", "", ""),
                RqMtFakeTest.contentLengthHeader(0L),
                RqMtFakeTest.contentDispositionHeader(
                    String.format(RqMtFakeTest.FORM_DATA, "a.rar")
                )
            )
        );
        try {
            MatcherAssert.assertThat(
                new RqHeaders.Base(
                    multi.part(part).iterator().next()
                ).header(RqMtFakeTest.DISPOSITION),
                Matchers.hasItem("form-data; name=\"t4\"")
            );
            MatcherAssert.assertThat(
                new RqPrint(
                    new RqHeaders.Base(
                        multi.part(part).iterator().next()
                    )
                ).printBody(),
                Matchers.allOf(
                    Matchers.startsWith("40 N"),
                    Matchers.endsWith("CA 94085")
                )
            );
        } finally {
            multi.part(part).iterator().next().body().close();
        }
    }

    /**
     * RqMtFake can close all parts once the request body has been
     * closed.
     * @throws Exception If some problem inside
     */
    @Test
    public void closesAllParts() throws Exception {
        final String body = "RqMtFakeTest.closesAllParts";
        final RqMultipart request = new RqMtFake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                RqMtFakeTest.contentLengthHeader(
                    (long) body.getBytes().length
                ),
                RqMtFakeTest.contentDispositionHeader(
                    "form-data; name=\"name\""
                )
            ),
            new RqWithHeaders(
                new RqFake("", "", body),
                RqMtFakeTest.contentLengthHeader(0L),
                RqMtFakeTest.contentDispositionHeader(
                    "form-data; name=\"content\"; filename=\"a.bin\""
                )
            )
        );
        final String exmessage =
            "An IOException was expected since the Stream is closed";
        final String name = "name";
        final String closed = "Closed";
        final String content = "content";
        final RqMtBase multi = new RqMtBase(request);
        multi.part(name).iterator().next().body().read();
        multi.part(content).iterator().next().body().read();
        multi.body().close();
        MatcherAssert.assertThat(
            multi.part(name).iterator().next(),
            Matchers.notNullValue()
        );
        try {
            multi.part(name).iterator().next().body().read();
            Assert.fail(exmessage);
        } catch (final IOException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.containsString(closed)
            );
        }
        MatcherAssert.assertThat(
            multi.part(content).iterator().next(),
            Matchers.notNullValue()
        );
        try {
            multi.part(content).iterator().next().body().read();
            Assert.fail(exmessage);
        } catch (final IOException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.containsString(closed)
            );
        }
    }

    /**
     * RqMtFake can close all parts explicitly even if the request body
     * has been closed.
     * <p>For backward compatibility reason we need to ensure that we don't get
     * {@code IOException} when we close explicitly a part even after closing
     * the input stream of the main request.
     * @throws Exception If some problem inside
     */
    @Test
    public void closesExplicitlyAllParts() throws Exception {
        final String body = "RqMtFakeTest.closesExplicitlyAllParts";
        final RqMultipart request = new RqMtFake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                RqMtFakeTest.contentLengthHeader(
                    (long) body.getBytes().length
                ),
                RqMtFakeTest.contentDispositionHeader(
                    "form-data; name=\"foo\""
                )
            ),
            new RqWithHeaders(
                new RqFake("", "", body),
                RqMtFakeTest.contentLengthHeader(0L),
                RqMtFakeTest.contentDispositionHeader(
                    "form-data; name=\"bar\"; filename=\"a.bin\""
                )
            )
        );
        final String foo = "foo";
        final String bar = "bar";
        final RqMtBase multi = new RqMtBase(request);
        multi.body().close();
        MatcherAssert.assertThat(
            multi.part(foo).iterator().next(),
            Matchers.notNullValue()
        );
        multi.part(foo).iterator().next().body().close();
        MatcherAssert.assertThat(
            multi.part(bar).iterator().next(),
            Matchers.notNullValue()
        );
        multi.part(bar).iterator().next().body().close();
    }

    /**
     * RqMtFake can return empty iterator on invalid part request.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnsEmptyIteratorOnInvalidPartRequest() throws IOException {
        final String body = "443 N Wolfe Rd, Sunnyvale, CA 94085";
        final RqMultipart multi = new RqMtFake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                RqMtFakeTest.contentLengthHeader(
                    (long) body.getBytes().length
                ),
                RqMtFakeTest.contentDispositionHeader(
                    "form-data; name=\"t5\""
                )
            ),
            new RqWithHeaders(
                new RqFake("", "", ""),
                RqMtFakeTest.contentLengthHeader(0L),
                RqMtFakeTest.contentDispositionHeader(
                    String.format(RqMtFakeTest.FORM_DATA, "a.zip")
                )
            )
        );
        MatcherAssert.assertThat(
            multi.part("fake").iterator().hasNext(),
            Matchers.is(false)
        );
        multi.body().close();
    }

    /**
     * RqMtFake can return correct name set.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnsCorrectNamesSet() throws IOException {
        final String body = "441 N Wolfe Rd, Sunnyvale, CA 94085";
        final RqMultipart multi = new RqMtFake(
            new RqFake(),
            new RqWithHeaders(
                new RqFake("", "", body),
                RqMtFakeTest.contentLengthHeader(
                    (long) body.getBytes().length
                ),
                RqMtFakeTest.contentDispositionHeader(
                    "form-data; name=\"address\""
                )
            ),
            new RqWithHeaders(
                new RqFake("", "", ""),
                RqMtFakeTest.contentLengthHeader(0L),
                RqMtFakeTest.contentDispositionHeader(
                    String.format(RqMtFakeTest.FORM_DATA, "a.bin")
                )
            )
        );
        try {
            MatcherAssert.assertThat(
                multi.names(),
                Matchers.<Iterable<String>>equalTo(
                    new HashSet<String>(Arrays.asList("address", "data"))
                )
            );
        } finally {
            multi.body().close();
        }
    }

    /**
     * Format Content-Disposition header.
     * @param dsp Disposition
     * @return Content-Disposition header
     */
    private static String contentDispositionHeader(final String dsp) {
        return String.format("Content-Disposition: %s", dsp);
    }

    /**
     * Format Content-Length header.
     * @param length Body length
     * @return Content-Length header
     */
    private static String contentLengthHeader(final long length) {
        return String.format("Content-Length: %d", length);
    }
}
