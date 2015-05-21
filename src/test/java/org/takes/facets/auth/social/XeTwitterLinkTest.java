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
package org.takes.facets.auth.social;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.takes.rs.xe.RsXembly;
import org.takes.rs.xe.XeAppend;

/**
 * Test case for {@link XeTwitterLink}.
 * @author Igor Khvostenkov (ikhvostenkov@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class XeTwitterLinkTest {

    /**
     * XeTwitterLink can generate a correct link.
     * @throws IOException If some problem inside
     */
    @Test
    public void generatesCorrectLink() throws IOException {
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "root",
                        new XeTwitterLink("testToken")
                    )
                ).body()
            ),
            XhtmlMatchers.hasXPaths(
                "/root/links/link[@rel='takes:twitter']"
            )
        );
    }

    /**
     * XeTwitterLink can compare objects.
     * @throws Exception If some problem inside
     */
    @Test
    public void compareObjectsTest() throws Exception {
        EqualsVerifier.forClass(XeTwitterLink.class)
            .suppress(Warning.TRANSIENT_FIELDS)
            .withRedefinedSuperclass()
            .verify();
    }
}
