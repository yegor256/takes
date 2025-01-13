/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link RsPrettyJson}.
 * @since 1.0
 */
final class RsPrettyJsonTest {

    @Test
    void formatsJsonBody() throws Exception {
        MatcherAssert.assertThat(
            new RsBodyPrint(
                new RsPrettyJson(
                    new RsWithBody("{\"widget\": {\"debug\": \"on\" }}")
                )
            ).asString(),
            Matchers.is(
                "{\n    \"widget\": {\n        \"debug\": \"on\"\n    }\n}"
            )
        );
    }

    @Test
    void rejectsNonJsonBody() {
        Assertions.assertThrows(
            IOException.class,
            () -> new RsBodyPrint(new RsPrettyJson(new RsWithBody("foo"))).asString()
        );
    }

    @Test
    void reportsCorrectContentLength() throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Writer w = new OutputStreamWriter(baos)) {
            w.write(
                new RsBodyPrint(
                    new RsWithBody(
                        "{\n    \"test\": {\n        \"test\": \"test\"\n    }\n}"
                    )
                ).asString()
            );
        }
        MatcherAssert.assertThat(
            new RsHeadPrint(
                new RsPrettyJson(
                    new RsWithBody("{\"test\": {\"test\": \"test\" }}")
                )
            ).asString(),
            Matchers.containsString(
                String.format(
                    "Content-Length: %d",
                    baos.toByteArray().length
                )
            )
        );
    }

    @Test
    void mustEvaluateTrueEquality() {
        final String body = "{\"person\":{\"name\":\"John\"}}";
        new Assertion<>(
            "Must evaluate true equality",
            new RsPrettyJson(
                new RsWithBody(body)
            ),
            new IsEqual<>(
                new RsPrettyJson(
                    new RsWithBody(body)
                )
            )
        ).affirm();
    }
}
