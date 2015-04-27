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

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.util.Iterator;
import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.takes.facets.auth.Identity;
import org.takes.misc.Href;
import org.takes.rq.RqFake;

/**
 * Test case for {@link PsLinkedin}.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.11.3
 * @checkstyle MagicNumberCheck (500 lines)
 */
public final class PsLinkedinTest {

    /**
     * Wire stub server.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final WireMockRule wire = new WireMockRule(8089);

    /**
     * PsLinkedin can login.
     * @throws IOException If some problem inside
     */
    @Test
    public void logins() throws IOException {
        final String code = RandomStringUtils.randomAlphanumeric(10);
        final String lapp = RandomStringUtils.randomAlphanumeric(10);
        final String lkey = RandomStringUtils.randomAlphanumeric(10);
        final String identifier = RandomStringUtils.randomAlphanumeric(10);
        WireMock.stubFor(
            WireMock.post(WireMock.urlMatching("/linkedin/token.*"))
                .withHeader("Accept", WireMock.equalTo("application/xml"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withBody(
                            String.format(
                                "{\"access_token\":\"%s\"}",
                                RandomStringUtils.randomAlphanumeric(10)
                            )
                        )
                )
        );
        WireMock.stubFor(
            WireMock.get(WireMock.urlMatching("/linkedin/api.*"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withBody(String.format("{\"id\":\"%s\"}", identifier))
                )
        );
        final Iterator<Identity> identity =
            new PsLinkedin(
                new Href("http://localhost:8089/linkedin/token"),
                new Href("http://localhost:8089/linkedin/api"),
                lapp,
                lkey
            ).enter(
                new RqFake(
                    "GET",
                    String.format("?code=%s", code)
                )
            );
        MatcherAssert.assertThat(
            identity.next().urn(),
            CoreMatchers.equalTo(String.format("urn:linkedin:%s", identifier))
        );
    }
}
