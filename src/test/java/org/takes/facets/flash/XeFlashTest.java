/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.flash;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rq.RqWithHeaders;
import org.takes.rs.RsXslt;
import org.takes.rs.xe.RsXembly;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeStylesheet;

/**
 * Test case for {@link XeFlash}.
 * @since 0.4
 */
final class XeFlashTest {

    @Test
    void acceptsRsFlashCookie() throws IOException {
        final Pattern pattern = Pattern.compile(
            "^Set-Cookie: RsFlash=(.*?);Path.*"
        );
        final Iterator<String> itr = new RsFlash("hello").head().iterator();
        final List<String> cookies = new ArrayList<>(0);
        while (itr.hasNext()) {
            final Matcher matcher = pattern.matcher(itr.next());
            if (matcher.find()) {
                cookies.add(matcher.group(1));
            }
        }
        MatcherAssert.assertThat(cookies, Matchers.hasSize(1));
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "root",
                        new XeFlash(
                            new RqWithHeader(
                                new RqFake(),
                                "Cookie",
                                "RsFlash=".concat(cookies.get(0))
                            )
                        )
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/root/flash[message='hello']",
                "/root/flash[level='INFO']"
            )
        );
    }

    @Test
    void rendersViaStandardXsltTemplate() throws IOException {
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsXslt(
                    new RsXembly(
                        new XeStylesheet(
                            "/org/takes/facets/flash/test_flash.xsl"
                        ),
                        new XeAppend(
                            "page",
                            new XeFlash(
                                new RqWithHeaders(
                                    new RqFake(),
                                    "Cookie: RsFlash=how are you?/INFO"
                                )
                            )
                        )
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/xhtml:html/xhtml:p[.='how are you?']",
                "/xhtml:html/xhtml:p[@class='flash flash-INFO']"
            )
        );
    }
}
