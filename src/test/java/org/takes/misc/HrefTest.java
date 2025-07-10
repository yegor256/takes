/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.misc;

import java.nio.charset.Charset;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasValues;

/**
 * Test case for {@link Href}.
 * @since 0.7
 */
final class HrefTest {

    @Test
    void buildsUri() {
        Assumptions.assumeTrue("UTF-8".equals(Charset.defaultCharset().name()));
        MatcherAssert.assertThat(
            new Href("http://example.com?%D0%B0=8&b=9")
                .with("д", "hello")
                .without("b")
                .with("b", "test")
                .toString(),
            Matchers.equalTo("http://example.com/?b=test&%D0%B0=8&%D0%B4=hello")
        );
    }

    @Test
    void buildsUriFromEmpty() {
        Assumptions.assumeTrue("UTF-8".equals(Charset.defaultCharset().name()));
        MatcherAssert.assertThat(
            new Href().path("boom-4").with("д", "").toString(),
            Matchers.equalTo("/boom-4?%D0%B4")
        );
    }

    @Test
    void buildsUriWithoutParams() {
        final String uri = "http://a.example.com";
        MatcherAssert.assertThat(
            new Href(uri).toString(),
            Matchers.equalTo("http://a.example.com/")
        );
    }

    @Test
    void extractsParametersFromQuery() {
        final String uri = "http://a.example.com?param1=hello&param2=world&param3=hello%20world";
        MatcherAssert.assertThat(
            "Can't get first parameter.",
            new Href(uri).param("param1"),
            new HasValues<>("hello")
        );
        MatcherAssert.assertThat(
            "Can't get second parameter.",
            new Href(uri).param("param2"),
            new HasValues<>("world")
        );
        MatcherAssert.assertThat(
            "Can't extract correctly escaped sequences in parameter value.",
            new Href(uri).param("param3"),
            new HasValues<>("hello world")
        );
    }

    @Test
    void addsPath() {
        Assumptions.assumeTrue("UTF-8".equals(Charset.defaultCharset().name()));
        MatcherAssert.assertThat(
            new Href("http://example.com").path("д").path("d").toString(),
            Matchers.equalTo("http://example.com/%D0%B4/d")
        );
        MatcherAssert.assertThat(
            new Href("http://example.com/").path("а").path("f").toString(),
            Matchers.equalTo("http://example.com/%D0%B0/f")
        );
    }

    @Test
    void acceptsEncodedQuery() {
        final String url = "http://localhost/read?file=%5B%5D%28%29.txt";
        MatcherAssert.assertThat(
            new Href(url).toString(),
            Matchers.equalTo(url)
        );
        MatcherAssert.assertThat(
            new Href(url).param("file").iterator().next(),
            Matchers.equalTo("[]().txt")
        );
    }

    @Test
    void acceptsNonProperlyEncodedUrl() {
        MatcherAssert.assertThat(
            new Href("http://www.netbout.com/[foo/bar]/read?file=%5B%5D%28%29.txt").toString(),
            Matchers.equalTo("http://www.netbout.com/%5Bfoo/bar%5D/read?file=%5B%5D%28%29.txt")
        );
    }

    @Test
    void buildsUriWithFragmentAndParams() {
        MatcherAssert.assertThat(
            new Href("http://example.com/%D0%B0/?a=1#hello").toString(),
            Matchers.equalTo("http://example.com/%D0%B0/?a=1#hello")
        );
    }

    @Test
    void buildsUriWithFragmentAndNoParams() {
        MatcherAssert.assertThat(
            new Href("http://example.com/#hello").toString(),
            Matchers.equalTo("http://example.com/#hello")
        );
    }
}
