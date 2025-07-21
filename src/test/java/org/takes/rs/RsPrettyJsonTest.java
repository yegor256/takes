/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
            "Pretty JSON formatter must format JSON with proper indentation",
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
            "Pretty JSON response must report correct content length",
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
