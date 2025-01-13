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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.misc.StateAwareInputStream;

/**
 * Test case for {@link RsVelocity}.
 * @since 0.1
 */
final class RsVelocityTest {

    @Test
    void buildsTextResponse() throws IOException {
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsVelocity(
                    "hello, ${name}!",
                    new RsVelocity.Pair("name", "Jeffrey")
                ).body(),
                StandardCharsets.UTF_8
            ),
            Matchers.equalTo("hello, Jeffrey!")
        );
    }

    @Test
    void closesTemplateInputStream() throws IOException {
        final String template = "hello, world!";
        final StateAwareInputStream stream = new StateAwareInputStream(
            IOUtils.toInputStream(template, StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsVelocity(
                    stream,
                    Collections.emptyMap()
                ).body(),
                StandardCharsets.UTF_8
            ),
            Matchers.equalTo(template)
        );
        MatcherAssert.assertThat(stream.isClosed(), Matchers.is(true));
    }

    @Test
    void useTemplateFolder() throws IOException {
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsVelocity(
                    RsVelocityTest.class.getResource(
                        "/vtl"
                    ).getPath(),
                    RsVelocityTest.class.getResourceAsStream(
                        "/vtl/simple.vm"
                    ),
                    new HashMap<>()
                ).body(),
                StandardCharsets.UTF_8
            ),
            Matchers.equalTo("Hello World!\n")
        );
    }
}
