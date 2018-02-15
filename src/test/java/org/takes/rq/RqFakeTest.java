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
package org.takes.rq;

import java.io.IOException;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;

/**
 * Test case for {@link RqFake}.
 * @author Dragan Bozanovic (bozanovicdr@gmail.com)
 * @version $Id$
 * @since 0.24
 */
public final class RqFakeTest {

    /**
     * Can conform to the Object.equals() contract.
     */
    @Test
    public void conformsToEquality() {
        EqualsVerifier.forClass(RqFake.class)
            .suppress(Warning.TRANSIENT_FIELDS)
            .withRedefinedSuperclass()
            .verify();
    }

    /**
     * RqFake can print correctly.
     * @throws IOException If some problem inside
     */
    @Test
    public void printsCorrectly() throws IOException {
        final RqFake req = new RqFake(
            "GET",
            "/just-a-test",
            "test-6=alpha"
        );
        MatcherAssert.assertThat(
            new RqPrint(req).print(),
            Matchers.allOf(
                Matchers.containsString("GET /just-a-test\r\n"),
                Matchers.endsWith("=alpha")
            )
        );
    }

    /**
     * RqFake can print body only once.
     * @throws IOException If some problem inside
     */
    @Test
    public void printsBodyOnlyOnce() throws IOException {
        final String body = "the body text";
        final Request req = new RqFake("", "", body);
        MatcherAssert.assertThat(
            new RqPrint(req).print(),
            Matchers.containsString(body)
        );
        MatcherAssert.assertThat(
            new RqPrint(req).print(),
            Matchers.not(Matchers.containsString(body))
        );
    }

}
