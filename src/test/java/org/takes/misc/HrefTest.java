/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
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
@SuppressWarnings("PMD.TooManyMethods")
final class HrefTest {

    @Test
    void buildsUri() {
        Assumptions.assumeTrue("UTF-8".equals(Charset.defaultCharset().name()));
        MatcherAssert.assertThat(
            "URI must be built correctly with added, removed and replaced parameters",
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
            "URI must be built correctly from empty base with path and empty parameter",
            new Href().path("boom-4").with("д", "").toString(),
            Matchers.equalTo("/boom-4?%D0%B4")
        );
    }

    @Test
    void buildsUriWithoutParams() {
        MatcherAssert.assertThat(
            "URI without parameters must include trailing slash",
            new Href("http://a.example.com").toString(),
            Matchers.equalTo("http://a.example.com/")
        );
    }

    @Test
    void extractsFirstParameter() {
        MatcherAssert.assertThat(
            "Cant get first parameter",
            new Href("http://a.example.com?param1=hello&param2=world").param("param1"),
            new HasValues<>("hello")
        );
    }

    @Test
    void extractsSecondParameter() {
        MatcherAssert.assertThat(
            "Cant get second parameter",
            new Href("http://a.example.com?param1=hello&param2=world").param("param2"),
            new HasValues<>("world")
        );
    }

    @Test
    void extractsEscapedParameter() {
        MatcherAssert.assertThat(
            "Cant extract correctly escaped sequences in parameter value",
            new Href("http://a.example.com?param3=hello%20world").param("param3"),
            new HasValues<>("hello world")
        );
    }

    @Test
    void addsPathWithEncoding() {
        Assumptions.assumeTrue("UTF-8".equals(Charset.defaultCharset().name()));
        MatcherAssert.assertThat(
            "Path segments must be URL-encoded when added",
            new Href("http://example.com").path("д").path("d").toString(),
            Matchers.equalTo("http://example.com/%D0%B4/d")
        );
    }

    @Test
    void addsPathWithTrailingSlash() {
        Assumptions.assumeTrue("UTF-8".equals(Charset.defaultCharset().name()));
        MatcherAssert.assertThat(
            "Path segments must be URL-encoded when base has trailing slash",
            new Href("http://example.com/").path("а").path("f").toString(),
            Matchers.equalTo("http://example.com/%D0%B0/f")
        );
    }

    @Test
    void preservesEncodedQuery() {
        MatcherAssert.assertThat(
            "Encoded query parameters must be preserved",
            new Href("http://localhost/read?file=%5B%5D%28%29.txt").toString(),
            Matchers.equalTo("http://localhost/read?file=%5B%5D%28%29.txt")
        );
    }

    @Test
    void decodesEncodedParameterValue() {
        MatcherAssert.assertThat(
            "Encoded parameter values must be decoded when retrieved",
            new Href("http://localhost/read?file=%5B%5D%28%29.txt")
                .param("file").iterator().next(),
            Matchers.equalTo("[]().txt")
        );
    }

    @Test
    void acceptsNonProperlyEncodedUrl() {
        MatcherAssert.assertThat(
            "Non-properly encoded URL characters must be encoded",
            new Href("http://www.netbout.com/[foo/bar]/read?file=%5B%5D%28%29.txt").toString(),
            Matchers.equalTo("http://www.netbout.com/%5Bfoo/bar%5D/read?file=%5B%5D%28%29.txt")
        );
    }

    @Test
    void buildsUriWithFragmentAndParams() {
        MatcherAssert.assertThat(
            "URI with fragment and parameters must be preserved",
            new Href("http://example.com/%D0%B0/?a=1#hello").toString(),
            Matchers.equalTo("http://example.com/%D0%B0/?a=1#hello")
        );
    }

    @Test
    void buildsUriWithFragmentAndNoParams() {
        MatcherAssert.assertThat(
            "URI with fragment but no parameters must be preserved",
            new Href("http://example.com/#hello").toString(),
            Matchers.equalTo("http://example.com/#hello")
        );
    }
}
