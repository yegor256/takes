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
package org.takes.misc;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Href}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.7
 */
public final class HrefTest {

    /**
     * Href can build an URI.
     */
    @Test
    public void buildsUri() {
        MatcherAssert.assertThat(
            new Href("http://example.com?%D0%B0=8&b=9")
                .with("д", "hello")
                // @checkstyle MultipleStringLiteralsCheck (1 line)
                .without("b")
                .with("b", "test")
                .toString(),
            Matchers.equalTo("http://example.com/?b=test&%D0%B0=8&%D0%B4=hello")
        );
    }

    /**
     * Href can build an URI from empty start.
     */
    @Test
    public void buildsUriFromEmpty() {
        MatcherAssert.assertThat(
            new Href().path("boom-4").with("д", "").toString(),
            Matchers.equalTo("/boom-4?%D0%B4")
        );
    }

    /**
     * Href can build an URI without params.
     */
    @Test
    public void buildsUriWithoutParams() {
        final String uri = "http://a.example.com";
        MatcherAssert.assertThat(
            new Href(uri).toString(),
            Matchers.equalTo("http://a.example.com/")
        );
    }

    /**
     * Href can add path.
     */
    @Test
    public void addsPath() {
        MatcherAssert.assertThat(
            new Href("http://example.com").path("д").path("d").toString(),
            Matchers.equalTo("http://example.com/%D0%B4/d")
        );
        MatcherAssert.assertThat(
            new Href("http://example.com/").path("а").path("f").toString(),
            Matchers.equalTo("http://example.com/%D0%B0/f")
        );
    }

    /**
     * Href can accept encoded query part.
     */
    @Test
    public void acceptsEncodedQuery() {
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

    /**
     * Href can accept non properly encoded URL.
     */
    @Test
    public void acceptsNonProperlyEncodedUrl() {
        MatcherAssert.assertThat(
            // @checkstyle LineLength (2 lines)
            new Href("http://www.netbout.com/[foo/bar]/read?file=%5B%5D%28%29.txt").toString(),
            Matchers.equalTo("http://www.netbout.com/%5Bfoo/bar%5D/read?file=%5B%5D%28%29.txt")
        );
    }

    /**
     * Href can build an URI with fragment.
     */
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    @Test
    public void buildsUriWithFragmentAndParams() {
        MatcherAssert.assertThat(
            new Href("http://example.com/%D0%B0/?a=1#hello").toString(),
            Matchers.equalTo("http://example.com/%D0%B0/?a=1#hello")
        );
    }

    /**
     * Href can build an URI with fragment and no params.
     */
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    @Test
    public void buildsUriWithFragmentAndNoParams() {
        MatcherAssert.assertThat(
            new Href("http://example.com/#hello").toString(),
            Matchers.equalTo("http://example.com/#hello")
        );
    }
}
