/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsEmpty;

/**
 * Test case for {@link FkContentType}.
 * @since 1.0
 */
final class FkContentTypeTest {

    /**
     * Content-Type header.
     */
    private static final String CONTENT_TYPE = "Content-Type";

    /**
     * Content-Type value.
     */
    private static final String CTYPE = "text/html charset=iso-8859-1";

    @Test
    void matchesWithAnyTypes() throws Exception {
        MatcherAssert.assertThat(
            new FkContentType("text/xml", new RsEmpty()).route(
                new RqWithHeader(
                    new RqFake(),
                    FkContentTypeTest.CONTENT_TYPE,
                    "*/* "
                )
            ).has(),
            Matchers.is(true)
        );
    }

    @Test
    void matchesDifferentTypes() throws Exception {
        MatcherAssert.assertThat(
            new FkContentType(
                "application/json charset=utf-8",
                FkContentTypeTest.emptyResponse()
            ).route(
                new RqWithHeader(
                    new RqFake(),
                    FkContentTypeTest.CONTENT_TYPE,
                    "images/*"
                )
            ).has(),
            Matchers.is(false)
        );
    }

    @Test
    void matchesIdenticalTypes() throws Exception {
        MatcherAssert.assertThat(
            new FkContentType(
                FkContentTypeTest.CTYPE,
                FkContentTypeTest.emptyResponse()
            ).route(
                new RqWithHeader(
                    new RqFake(),
                    FkContentTypeTest.CONTENT_TYPE,
                    FkContentTypeTest.CTYPE
                )
            ).has(),
            Matchers.is(true)
        );
    }

    @Test
    void matchesEmptyType() throws Exception {
        MatcherAssert.assertThat(
            new FkContentType(
                "*/*",
                FkContentTypeTest.emptyResponse()
            ).route(
                new RqWithHeader(
                    new RqFake(), FkContentTypeTest.CONTENT_TYPE, ""
                )
            ).has(), Matchers.is(true)
        );
    }

    @Test
    void matchesDifferentEncodingsTypes() throws Exception {
        MatcherAssert.assertThat(
            new FkContentType(
                FkContentTypeTest.CTYPE,
                FkContentTypeTest.emptyResponse()
            ).route(
                new RqWithHeader(
                    new RqFake(),
                    FkContentTypeTest.CONTENT_TYPE,
                    "text/html charset=utf8"
                )
            ).has(),
            Matchers.is(false)
        );
    }

    @Test
    void mustEvaluateEqualsTest() {
        final Take take = req -> new RsEmpty();
        final String type = "text/xml";
        new Assertion<>(
            "Must evaluate true equality",
            new FkContentType(type, take),
            new IsEqual<>(new FkContentType(type, take))
        ).affirm();
    }

    /**
     * Create a Take instance with empty response.
     * @return Take
     */
    private static Take emptyResponse() {
        return req -> new RsEmpty();
    }
}
